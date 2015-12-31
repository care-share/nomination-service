package careshare.nominationService.model;

import java.util.Collection;

public class ChangeRequest {

    private String carePlanId;
    private String authorId;
    private Collection<Nomination> conditions;
    private Collection<Nomination> goals;
    private Collection<Nomination> medicationOrders;
    private Collection<Nomination> nutritionOrders;
    private Collection<Nomination> procedureRequests;

    public ChangeRequest() {
    }

    public ChangeRequest(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public ChangeRequest(
            String carePlanId,
            String authorId,
            Collection<Nomination> conditions,
            Collection<Nomination> goals,
            Collection<Nomination> medicationOrders,
            Collection<Nomination> nutritionOrders,
            Collection<Nomination> procedureRequests) {
        this.carePlanId = carePlanId;
        this.authorId = authorId;
        this.conditions = conditions;
        this.goals = goals;
        this.medicationOrders = medicationOrders;
        this.nutritionOrders = nutritionOrders;
        this.procedureRequests = procedureRequests;
    }

    public String getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Collection<Nomination> getConditions() {
        return conditions;
    }

    public void setConditions(Collection<Nomination> conditions) {
        this.conditions = conditions;
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

    public Collection<Nomination> getNutritionOrders() {
        return nutritionOrders;
    }

    public void setNutritionOrders(Collection<Nomination> nutritionOrders) {
        this.nutritionOrders = nutritionOrders;
    }

    public Collection<Nomination> getProcedureRequests() {
        return procedureRequests;
    }

    public void setProcedureRequests(Collection<Nomination> procedureRequests) {
        this.procedureRequests = procedureRequests;
    }
}
