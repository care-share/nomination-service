package careshare.nominationService.model;

import java.util.Collection;

public class ChangeRequest {

    private String carePlanId;
    private Collection<Nomination> conditions;
    private Collection<Nomination> diagnosticOrders;
    private Collection<Nomination> goals;
    private Collection<Nomination> medicationOrders;
    private Collection<Nomination> procedureRequests;

    public ChangeRequest() {
    }

    public ChangeRequest(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public ChangeRequest(String carePlanId, Collection<Nomination> conditions, Collection<Nomination> diagnosticOrders, Collection<Nomination> goals, Collection<Nomination> medicationOrders, Collection<Nomination> procedureRequests) {
        this.carePlanId = carePlanId;
        this.conditions = conditions;
        this.diagnosticOrders = diagnosticOrders;
        this.goals = goals;
        this.medicationOrders = medicationOrders;
        this.procedureRequests = procedureRequests;
    }

    public String getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public Collection<Nomination> getConditions() {
        return conditions;
    }

    public void setConditions(Collection<Nomination> conditions) {
        this.conditions = conditions;
    }

    public Collection<Nomination> getDiagnosticOrders() {
        return diagnosticOrders;
    }

    public void setDiagnosticOrders(Collection<Nomination> diagnosticOrders) {
        this.diagnosticOrders = diagnosticOrders;
    }

    public Collection<Nomination> getGoals() {
        return goals;
    }

    public void setGoals(Collection<Nomination> goals) {
        this.goals = goals;
    }

    public Collection<Nomination> getMedicationOrders() {
        return medicationOrders;
    }

    public void setMedicationOrders(Collection<Nomination> medicationOrders) {
        this.medicationOrders = medicationOrders;
    }

    public Collection<Nomination> getProcedureRequests() {
        return procedureRequests;
    }

    public void setProcedureRequests(Collection<Nomination> procedureRequests) {
        this.procedureRequests = procedureRequests;
    }
}
