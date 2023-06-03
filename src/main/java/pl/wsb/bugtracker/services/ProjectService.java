package pl.wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.wsb.bugtracker.models.Project;
import pl.wsb.bugtracker.repositories.ProjectRepository;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    final private ProjectRepository projectRepository;

    public Page<Project> findAll(Specification<Project> specification, Pageable pageable) {
        return projectRepository.findAll(specification, pageable);
    }

    public void save(Project project) {
        if (project.getDateCreated() == null) {
            project.setDateCreated(new Date());
        }
        projectRepository.save(project);
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public Optional<Project> findById(Long id) {return projectRepository.findById(id);}
}
