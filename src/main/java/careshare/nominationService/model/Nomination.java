package careshare.nominationService.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import javax.persistence.*;

@Entity
public class Nomination implements Serializable {
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";

    // composite primary key, see http://www.objectdb.com/java/jpa/entity/id#Composite_Primary_Key_
    @Id @GeneratedValue private Long id; // generated ID of this specific nomination
    private String authorId; // CareAuth User ID of the person who authored this nomination
    private String resourceId; // ID of the FHIR Resource that the author has nominated a change for

    private String carePlanId; // ID of the FHIR CarePlan that this nomination applies to
    private String patientId; // ID of the FHIR Patient that this nomination applies to
    private Date timestamp; // when this Nomination was last updated
    private String action;

    @JsonIgnore
    private String resourceType;

    @JsonRawValue
    @Column(length = 65536)
    private String existing;

    @JsonRawValue
    @Column(length = 65536)
    private String proposed;

    @JsonRawValue
    @Column(length = 65536)
    private String diff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(String carePlanId) {
        this.carePlanId = carePlanId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Nomination(String authorId, String resourceId, String carePlanId, String patientId, String action,
                      String resourceType, String existing, String proposed, String diff) {
        this.authorId = authorId;
        this.resourceId = resourceId;
        this.carePlanId = carePlanId;
        this.patientId = patientId;
        this.timestamp = new Date(); // set timestamp to whenever this nomination was first created
        this.action = action;
        this.resourceType = resourceType;
        this.existing = existing;
        this.proposed = proposed;
        this.diff = diff;
    }

    public Nomination() {
    }

    // this is used to find the Nomination with the newest timestamp
    public static Comparator<Nomination> TimestampComparator = new Comparator<Nomination>() {
        public int compare(Nomination item1, Nomination item2) {
            // ascending order
            return item1.timestamp.compareTo(item2.timestamp);
        }
    };

    // this is used to compare a newly-created nomination with existing nominations
    // it is assumed that any two being compared will already have the same resourceId and authorId
    // if two nominations are equal, the new one will overwrite the old one
    public static Comparator<Nomination> DiffComparator = new Comparator<Nomination>() {
        public int compare(Nomination item1, Nomination item2) {
            int result = Integer.MIN_VALUE; // not equal
            if (item1.action != null && item2.action != null && item1.action.equals(item2.action)) {
                if (ACTION_UPDATE.equals(item1.action)) {
                    ObjectMapper mapper = new ObjectMapper();
                    // TODO: store Nomination "diff" attributes as Diff objects instead of String objects
                    // this will avoid using the ObjectMapper multiple times and increase performance
                    try {
                        Diff item1Diff = mapper.readValue(item1.diff, Diff.class);
                        Diff item2Diff = mapper.readValue(item2.diff, Diff.class);
                        // if each diff modifies the same path, they are equal
                        result = item1Diff.getPath().compareTo(item2Diff.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // this is a create or delete, there is no diff to compare
                    result = 0; // equal
                }
            }
            return result;
        }
    };
}
