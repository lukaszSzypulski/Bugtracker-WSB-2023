package wsb.bugtracker.audit;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@RevisionEntity(AuditingRevisionListener.class)
@Entity
public class AuditRevisionEntity extends DefaultRevisionEntity {

    @Getter
    @Setter
    private String actor;
}
