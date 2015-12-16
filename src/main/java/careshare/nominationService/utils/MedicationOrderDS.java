/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package careshare.nominationService.utils;

import careshare.nominationService.model.MedicationOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

/**
 *
 * @author kcrouch
 */
public class MedicationOrderDS extends JsonDeserializer<MedicationOrder> {

  @Override
  public MedicationOrder deserialize(JsonParser jp, DeserializationContext ctxt)
		  throws IOException, JsonProcessingException {
	return (MedicationOrder) DiffingDeserializer.deserialize(jp, ctxt, new MedicationOrder());
  }
}
