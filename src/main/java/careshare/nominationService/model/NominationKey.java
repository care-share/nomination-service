package careshare.nominationService.model;

import java.io.Serializable;

// Composite Primary Key Class
// See http://docs.oracle.com/javaee/5/tutorial/doc/bnbqa.html#bnbqg
// See http://www.objectdb.com/java/jpa/entity/id#Composite_Primary_Key_
public final class NominationKey implements Serializable {
    private Long id; // generated ID of this specific nomination
    public String authorId; // CareAuth User ID of the person who authored this nomination
    public String resourceId;

    public NominationKey() {}

    public NominationKey(Long id, String authorId, String resourceId) {
        this.id = id;
        this.authorId = authorId;
        this.resourceId = resourceId;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public boolean equals(Object otherOb) {
        if (this == otherOb) {
            return true;
        }
        if (!(otherOb instanceof NominationKey)) {
            return false;
        }
        NominationKey other = (NominationKey) otherOb;
        return (id == null ? other.id == null : id.equals(other.id))
                && (authorId == null ? other.authorId == null : authorId.equals(other.authorId))
                && (resourceId == null ? other.resourceId == null : resourceId.equals(other.resourceId));
    }

    public int hashCode() {
        return (authorId == null ? 0 : authorId.hashCode())
                ^ (resourceId == null ? 0 : resourceId.hashCode());
    }

    public String toString() {
        return String.format("%s/%s", authorId, resourceId);
    }
}
