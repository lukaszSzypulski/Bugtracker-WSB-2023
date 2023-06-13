package wsb.bugtracker.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuditingRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {

        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) revisionEntity;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String actor = user.getUsername();

        auditRevisionEntity.setActor(actor);
    }
}
