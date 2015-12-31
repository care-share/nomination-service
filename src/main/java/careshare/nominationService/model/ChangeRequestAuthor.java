package careshare.nominationService.model;

import java.util.Date;

public class ChangeRequestAuthor {

    private String authorId;
    private Date timestamp;

    public ChangeRequestAuthor() {
    }

    public ChangeRequestAuthor(String authorId, Date timestamp) {
        this.authorId = authorId;
        this.timestamp = timestamp;
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
}
