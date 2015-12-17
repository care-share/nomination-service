package careshare.nominationService.model;

import java.util.Collection;

public class ORCarePlan {

  private String id;
  private Collection<OneRing> conditions;
  private Collection<OneRing> diagnosticOrders;
  private Collection<OneRing> goals;
  private Collection<OneRing> medicationOrders;
  private Collection<OneRing> procedureRequests;

  public ORCarePlan() {
  }

  public ORCarePlan(String id) {
	this.id = id;
  }

  public ORCarePlan(String id, Collection<OneRing> conditions, Collection<OneRing> diagnosticOrders, Collection<OneRing> goals, Collection<OneRing> medicationOrders, Collection<OneRing> procedureRequests) {
	this.id = id;
	this.conditions = conditions;
	this.diagnosticOrders = diagnosticOrders;
	this.goals = goals;
	this.medicationOrders = medicationOrders;
	this.procedureRequests = procedureRequests;
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public Collection<OneRing> getConditions() {
	return conditions;
  }

  public void setConditions(Collection<OneRing> conditions) {
	this.conditions = conditions;
  }

  public Collection<OneRing> getDiagnosticOrders() {
	return diagnosticOrders;
  }

  public void setDiagnosticOrders(Collection<OneRing> diagnosticOrders) {
	this.diagnosticOrders = diagnosticOrders;
  }

  public Collection<OneRing> getGoals() {
	return goals;
  }

  public void setGoals(Collection<OneRing> goals) {
	this.goals = goals;
  }

  public Collection<OneRing> getMedicationOrders() {
	return medicationOrders;
  }

  public void setMedicationOrders(Collection<OneRing> medicationOrders) {
	this.medicationOrders = medicationOrders;
  }

  public Collection<OneRing> getProcedureRequests() {
	return procedureRequests;
  }

  public void setProcedureRequests(Collection<OneRing> procedureRequests) {
	this.procedureRequests = procedureRequests;
  }
}
