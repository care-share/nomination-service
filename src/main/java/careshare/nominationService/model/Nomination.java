package careshare.nominationService.model;

import careshare.nominationService.utils.NominationDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonDeserialize(using = NominationDeserializer.class)
public class Nomination implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String carePlanId;
    private String authorId; // CareAuth User ID of the person who authored this nomination
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

    public Nomination(String carePlanId, String authorId, String action, String resourceType, String existing, String proposed, String diff) {
        this.carePlanId = carePlanId;
        this.authorId = authorId;
        this.action = action;
        this.resourceType = resourceType;
        this.existing = existing;
        this.proposed = proposed;
        this.diff = diff;
    }

    public Nomination() {
    }

}
