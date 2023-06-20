package wsb.bugtracker.repositories;

import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wsb.bugtracker.models.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {

    @Transactional
    @Modifying
    @Query(value = "update Issue set attachment = null where id = ?1", nativeQuery = true)
    void findByIdAndSetAttachment(Long id);

    @Formula(value = "(SELECT count(*) FROM issue WHERE issue.project_id= ${id}")
    Long countIssueByProjectId(Long id);


}
