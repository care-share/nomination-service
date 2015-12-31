package careshare.nominationService.model;

import java.util.Date;
import java.util.List;

public class ChangeRequest {

    private String carePlanId;
    private String authorId;
    private Date timestamp;
    private List<Nomination> conditions;
    private List<Nomination> goals;
    private List<Nomination> medicationOrders;
    private List<Nomination> nutritionOrders;
    private List<Nomination> procedureRequests;

    public ChangeRequest() {
    }

    public ChangeRequest(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public ChangeRequest(
            String carePlanId,
            String authorId,
            Date timestamp,
            List<Nomination> conditions,
            List<Nomination> goals,
            List<Nomination> medicationOrders,
            List<Nomination> nutritionOrders,
            List<Nomination> procedureRequests) {
        this.carePlanId = carePlanId;
        this.authorId = authorId;
        this.timestamp = timestamp; // when the newest Nomination of this ChangeRequest was updated
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<Nomination> getConditions() {
        return conditions;
    }

    public void setConditions(List<Nomination> conditions) {
        this.conditions = conditions;
    }

    public List<Nomination> getGoals() {
        return goals;
    }

    public void setGoals(List<Nomination> goals) {
        this.goals = goals;
    }

    public List<Nomination> getMedicationOrders() {
        return medicationOrders;
    }

    public void setMedicationOrders(List<Nomination> medicationOrders) {
        this.medicationOrders = medicationOrders;
    }

    public List<Nomination> getNutritionOrders() {
        return nutritionOrders;
    }

    public void setNutritionOrders(List<Nomination> nutritionOrders) {
        this.nutritionOrders = nutritionOrders;
    }

    public List<Nomination> getProcedureRequests() {
        return procedureRequests;
    }

    public void setProcedureRequests(List<Nomination> procedureRequests) {
        this.procedureRequests = procedureRequests;
    }
}
