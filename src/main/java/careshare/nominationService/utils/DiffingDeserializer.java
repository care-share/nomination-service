
package careshare.nominationService.utils;

import careshare.nominationService.model.BaseDeserializable;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import java.io.IOException;

/**
 *
 * @author kcrouch
 */
public class DiffingDeserializer {
  
  public static Object deserialize(JsonParser jp, DeserializationContext ctxt, BaseDeserializable what)throws IOException, JsonProcessingException {
  JsonNode node = jp.getCodec().readTree(jp);

	  String careplan = node.get("careplan").asText();
	  String action = node.get("action").asText();
	  JsonNode pNode = node.get("proposed");
	  JsonNode eNode = node.get("existing");
	  //JsonNode dNode = node.get("diff");
	  
	  String pString = pNode  == null ? null: pNode.toString();
	  String eString = eNode == null ? null : eNode.toString();
	  String dString = null;
	  JsonNode patch = null;
	  
	  if(pNode != null || eNode != null) 
		 patch = JsonDiff.asJson(eNode, pNode);
		
	  if(patch != null) 
		dString = patch.toString();
	  
	  what.setAction(action);
	  what.setCareplan(careplan);
	  what.setDiff(dString);
	  what.setExisting(eString);
	  what.setProposed(pString);
	  return what;
  }

  
  
}
