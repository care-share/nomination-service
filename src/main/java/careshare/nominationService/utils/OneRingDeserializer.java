package careshare.nominationService.utils;

import careshare.nominationService.model.OneRing;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import java.io.IOException;

/**
 *
 * @author kcrouch
 */
public class OneRingDeserializer extends JsonDeserializer<OneRing> {

  @Override
  public OneRing deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
	return diff(jp, dc, new OneRing());
  }

 protected OneRing diff(JsonParser jp, DeserializationContext ctxt,OneRing what)throws IOException, JsonProcessingException {
  JsonNode node = jp.getCodec().readTree(jp);
	  String action = node.get("action").asText();
	  JsonNode pNode = node.get("proposed");
	  JsonNode eNode = node.get("existing");
	  
	  String pString = pNode  == null ? null: pNode.toString();
	  String eString = eNode == null ? null : eNode.toString();
	  String dString = null;
	  JsonNode patch = null;
	  
	  if(pNode != null || eNode != null) 
		 patch = JsonDiff.asJson(eNode, pNode);
		
	  if(patch != null) 
		dString = patch.toString();

	  
	  what.setAction(action);
	  what.setDiff(dString);
	  what.setExisting(eString);
	  what.setProposed(pString);
	  return what;
  }
}
