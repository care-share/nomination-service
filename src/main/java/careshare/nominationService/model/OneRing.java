package careshare.nominationService.model;

import careshare.nominationService.utils.OneRingDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author kcrouch
 */

@Entity
@JsonDeserialize(using = OneRingDeserializer.class)
public class OneRing implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  private String careplan;
  private String action;
  
  @JsonIgnore
  private String nominationFor;

  @JsonRawValue
  @Column(length=65536)  
  private String existing;

  @JsonRawValue
  @Column(length=65536)  
  private String proposed;

  @JsonRawValue
  @Column(length=65536)  
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

  public void setCareplan(String careplan) {
	this.careplan = careplan;
  }

  public String getAction() {
	return action;
  }

  public void setAction(String action) {
	this.action = action;
  }

  public String getExisting() {
	return existing;
  }

  public void setExisting(String existing) {
	this.existing = existing;
  }

  public String getProposed() {
	return proposed;
  }

  public void setProposed(String proposed) {
	this.proposed = proposed;
  }

  public String getDiff() {
	return diff;
  }

  public void setDiff(String diff) {
	this.diff = diff;
  }

  public String getNominationFor() {
	return nominationFor;
  }

  public void setNominationFor(String nominationFor) {
	this.nominationFor = nominationFor;
  }

  public OneRing(String careplan, String action, String nominationFor, String existing, String proposed, String diff) {
	this.careplan = careplan;
	this.action = action;
	this.nominationFor = nominationFor;
	this.existing = existing;
	this.proposed = proposed;
	this.diff = diff;
  }

  public OneRing() {
  }

}
