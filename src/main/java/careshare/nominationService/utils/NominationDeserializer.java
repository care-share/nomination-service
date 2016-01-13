package careshare.nominationService.utils;

import careshare.nominationService.model.Nomination;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

import java.io.IOException;

public class NominationDeserializer extends JsonDeserializer<Nomination> {

    @Override
    public Nomination deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        return diff(jp, dc, new Nomination());
    }

    protected Nomination diff(JsonParser jp, DeserializationContext ctxt, Nomination what) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        String action = node.get("action").asText();
        JsonNode pNode = node.get("proposed");
        JsonNode eNode = node.get("existing");

        String pString = pNode == null ? null : pNode.toString();
        String eString = eNode == null ? null : eNode.toString();
        String dString = null;
        JsonNode patch = null;

	ObjectMapper mapper = new ObjectMapper();

        if (pNode != null || eNode != null)
            patch = JsonDiff.asJson(eNode, pNode);

        if (patch != null){
	    //	    ((ObjectNode)patch.get(0)).put("testfirstpath", patch.get(0).findValue("path").asText());
	    for (int i = 0; i < patch.size(); i++) {
		JsonNode attributePatch = patch.get(i);
		String attributePathString = attributePatch.findValue("path").asText();
		// iterate through the path, and walk a pointer down to the value of the patched attribute in the existing resource
		JsonNode pointer = eNode; 
		for (String pathStep: attributePathString.split("/")){
		    if (pathStep.equals("")) // pathStep is not a real step
			continue;
		    if (pathStep.matches("^-?\\d+$")){ // pathStep is an Array index
			pointer = pointer.get(Integer.parseInt(pathStep.trim()));
		    } else { // pathStep is a dictionary key
			pointer = pointer.get(pathStep);
		    }
		}
		// add the original attribute value info to the attribute patch
		((ObjectNode)attributePatch).put("originalValue", pointer);
	    }
            dString = patch.toString();
	}

        what.setAction(action);
        what.setDiff(dString);
        what.setExisting(eString);
        what.setProposed(pString);
        return what;
    }
}
