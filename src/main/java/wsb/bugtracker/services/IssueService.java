package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.repositories.IssueRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public Page<Issue> findAll(Specification<Issue> specification, Pageable pageable) {
        return issueRepository.findAll(specification, pageable);
    }

    public void delete(Long id) {
        issueRepository.deleteById(id);
    }

    public void save(Issue issue) {
        issueRepository.save(issue);
    }

    public Optional<Issue> findById(Long id) {
        return issueRepository.findById(id);
    }

    public void findByIdAndSetAttachment(Long id) {
        issueRepository.findByIdAndSetAttachment(id);
    }

    public Boolean isProjectAssigned(Long projectId) {
        return issueRepository.countIssueByProjectId(projectId) != 0;
    }
}
