
package careshare.nominationService.model;

import careshare.nominationService.utils.MedicationOrderDS;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 *
 * @author kcrouch
 */

@Entity
@JsonDeserialize(using = MedicationOrderDS.class)
public class MedicationOrder implements Serializable, BaseDeserializable {
  
  @Id
  @GeneratedValue
  private Long id;
  
  
  private String careplan;
  private String action;
  
  @JsonRawValue  
  private String existing;
  
  @JsonRawValue
  private String proposed;
  
  @JsonRawValue
  private String diff;

  public Long getId() {
	return id;
  }

  public void setId(Long id) {
	this.id = id;
  }

  public String getCareplan() {
	return careplan;
  }

  @Override
  public void setCareplan(String careplan) {
	this.careplan = careplan;
  }

  public String getAction() {
	return action;
  }

  @Override
  public void setAction(String action) {
	this.action = action;
  }

  public String getExisting() {
	return existing;
  }

  @Override
  public void setExisting(String existing) {
	this.existing = existing;
  }

  public String getProposed() {
	return proposed;
  }

  @Override
  public void setProposed(String proposed) {
	this.proposed = proposed;
  }

  public String getDiff() {
	return diff;
  }

  @Override
  public void setDiff(String diff) {
	this.diff = diff;
  }

  public MedicationOrder(String careplan, String action, String existing, String proposed, String diff) {
	this.careplan = careplan;
	this.action = action;
	this.existing = existing;
	this.proposed = proposed;
	this.diff = diff;
  }

  
  

  public MedicationOrder() {
  }


  
}
