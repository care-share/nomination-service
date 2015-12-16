
package careshare.nominationService.model;

/**
 *
 * @author kcrouch
 */
public interface BaseDeserializable {

  void setAction(String action);

  void setCareplan(String careplan);

  void setDiff(String diff);

  void setExisting(String existing);

  void setProposed(String proposed);
  
}
