package pl.wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wsb.bugtracker.models.Issue;
import pl.wsb.bugtracker.repositories.IssueRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssueService {

    final private IssueRepository issueRepository;

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public void delete(Long id) {
        issueRepository.deleteById(id);
    }

    public void save(Issue issue){
        issueRepository.save(issue);
    };

    public Optional<Issue> findById(Long id){return issueRepository.findById(id);}
}
