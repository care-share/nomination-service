package careshare.nominationService.model;

import java.util.Collection;


public class CarePlan  {
  
  private String id;
  private Collection<Condition> conditions;
  private Collection<DiagnosticOrder> diagnosticOrders;
  private Collection<Goal> goals;
  private Collection<MedicationOrder> medicationOrders;
  private Collection<ProcedureRequest> procedureRequests;
  
  public CarePlan() {
  }

  public CarePlan(String id) {
	this.id = id;
  }

  public CarePlan(String id, Collection<Condition> conditions, Collection<DiagnosticOrder> diagnosticOrders, Collection<Goal> goals, Collection<MedicationOrder> medicationOrders, Collection<ProcedureRequest> procedureRequests) {
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

  public Collection<Condition> getConditions() {
	return conditions;
  }

  public void setConditions(Collection<Condition> conditions) {
	this.conditions = conditions;
  }

  public Collection<Goal> getGoals() {
	return goals;
  }

  public void setGoals(Collection<Goal> goals) {
	this.goals = goals;
  }

  public Collection<DiagnosticOrder> getDiagnosticOrders() {
	return diagnosticOrders;
  }

  public void setDiagnosticOrders(Collection<DiagnosticOrder> diagnosticOrders) {
	this.diagnosticOrders = diagnosticOrders;
  }

  public Collection<MedicationOrder> getMedicationOrders() {
	return medicationOrders;
  }

  public void setMedicationOrders(Collection<MedicationOrder> medicationOrders) {
	this.medicationOrders = medicationOrders;
  }

  public Collection<ProcedureRequest> getProcedureRequests() {
	return procedureRequests;
  }

  public void setProcedureRequests(Collection<ProcedureRequest> procedureRequests) {
	this.procedureRequests = procedureRequests;
  }


  
  
  
}
