package careshare.nominationService.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author kcrouch
 */
@Entity

public class ProcedureRequest {

  @Id
  @GeneratedValue
  private Long id;
  
    private String careplan;

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

	

  protected ProcedureRequest() {
  }

}
