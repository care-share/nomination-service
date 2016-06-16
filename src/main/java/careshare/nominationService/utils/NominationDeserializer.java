/*
 * Copyright 2016 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package careshare.nominationService.utils;

import careshare.nominationService.model.Nomination;
import careshare.nominationService.model.NominationList;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.IOException;

public class NominationDeserializer extends JsonDeserializer<NominationList> {

    NominationList value;
    String authorId;
    String resourceId;
    String carePlanId;
    String patientId;
    String resourceType;
    String action;
    String proposed;
    String existing;

    @Override
    public NominationList deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        value = new NominationList();
        JsonNode node = jp.getCodec().readTree(jp);
        authorId = node.get("authorId").asText();
        resourceId = node.get("resourceId").asText();
        carePlanId = node.get("carePlanId").asText();
        patientId = node.get("patientId").asText();
        resourceType = node.get("resourceType").asText();
        action = node.get("action").asText();
        JsonNode proposedJson = node.get("proposed");
        JsonNode existingJson = node.get("existing");

        proposed = proposedJson == null ? null : proposedJson.toString();
        existing = existingJson == null ? null : existingJson.toString();
        String diff = "{}";
        JsonNode patch = null;

        if (Nomination.ACTION_UPDATE.equals(action)) {
            // only generate a diff string for Nominations that have an 'update' action
            if (proposedJson != null || existingJson != null)
                patch = JsonDiff.asJson(existingJson, proposedJson);

            if (patch != null) {
                for (int i = 0; i < patch.size(); i++) {
                    JsonNode attributePatch = patch.get(i);
                    String attributePathString = attributePatch.findValue("path").asText();
                    // iterate through the path, and walk a pointer down to the value of the patched attribute in the existing resource
                    JsonNode pointer = existingJson;
                    for (String pathStep : attributePathString.split("/")) {
                        if (pathStep.equals("")) // pathStep is not a real step
                            continue;
                        if (pathStep.matches("^-?\\d+$")) { // pathStep is an Array index
                            pointer = pointer.get(Integer.parseInt(pathStep.trim()));
                        } else { // pathStep is a dictionary key
                            pointer = pointer.get(pathStep);
                        }
                    }
                    // add the original attribute value info to the attribute patch
                    ((ObjectNode) attributePatch).put("originalValue", pointer);

                    // for each patched attribute, create a separate nomination
                    diff = attributePatch.toString();
                    generateNomination(diff);
                }
            }
        } else {
            generateNomination(diff);
        }

        return value;
    }

    private void generateNomination(String diff) {
        Nomination nomination = new Nomination(authorId, resourceId, carePlanId, patientId, action, resourceType, existing, proposed, diff);
        value.add(nomination);
    }
}
