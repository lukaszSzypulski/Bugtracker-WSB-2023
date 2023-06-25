package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.repositories.IssueRepository;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;


    public Page<Issue> findAll(Specification<Issue> specification, Pageable pageable) {
        return issueRepository.findAll(specification, pageable);
    }

    public void delete(Long id) {
        issueRepository.deleteById(id);
    }

    public void save(Issue issue) {
        if (issue.getDateCreated() == null || issue.getLastUpdated() == null) {
            if (issue.getDateCreated() == null) {
                issue.setDateCreated(new Date());
            }
            if (issue.getLastUpdated() == null) {
                issue.setLastUpdated(new Date());
            }
        }
        issueRepository.save(issue);
    }

    public Optional<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }

}
