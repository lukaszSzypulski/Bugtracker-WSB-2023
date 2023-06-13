package wsb.bugtracker.audit;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionType;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Status;

import java.util.Date;

@Getter
@Setter
public class AuditDataDTO {

    Date date;
    String actor;

    RevisionType revisionType;

    String title;
    Status status;

    public AuditDataDTO(Object[] revision) {
        AuditRevisionEntity auditedRevisionEntity = (AuditRevisionEntity) revision[1];

        this.date = new Date(auditedRevisionEntity.getTimestamp());
        this.actor = auditedRevisionEntity.getActor();

        this.revisionType = (RevisionType) revision[2];

        Issue issue = (Issue) revision[0];
        this.title = issue.getName();
        this.status = issue.getStatus();
    }
}
