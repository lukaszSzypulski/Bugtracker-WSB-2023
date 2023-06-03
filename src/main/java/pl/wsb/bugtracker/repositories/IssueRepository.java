package pl.wsb.bugtracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.bugtracker.models.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
}
