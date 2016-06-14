package careshare.nominationService.model;

import java.util.Date;
import java.util.List;

public class ChangeRequest {

    private String carePlanId;
    private String authorId;
    private String patientId;
    private Date timestamp;
    private List<Nomination> conditions;
    private List<Nomination> goals;
    private List<Nomination> procedureRequests;
    private List<Nomination> nutritionOrders;
//    private List<Nomination> medicationOrders;

    public ChangeRequest() {
    }

    public ChangeRequest(
            String carePlanId, String authorId, String patientId, Date timestamp, List<Nomination> conditions,
            List<Nomination> goals, List<Nomination> procedureRequests, List<Nomination> nutritionOrders
    ) {
        this.carePlanId = carePlanId;
        this.authorId = authorId;
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.conditions = conditions;
        this.goals = goals;
        this.procedureRequests = procedureRequests;
        this.nutritionOrders = nutritionOrders;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public List<Nomination> getProcedureRequests() {
        return procedureRequests;
    }

    public void setProcedureRequests(List<Nomination> procedureRequests) {
        this.procedureRequests = procedureRequests;
    }

    public List<Nomination> getNutritionOrders() {
        return nutritionOrders;
    }

    public void setNutritionOrders(List<Nomination> nutritionOrders) {
        this.nutritionOrders = nutritionOrders;
    }

//    public List<Nomination> getMedicationOrders() {
//        return medicationOrders;
//    }
//
//    public void setMedicationOrders(List<Nomination> medicationOrders) {
//        this.medicationOrders = medicationOrders;
//    }
}
