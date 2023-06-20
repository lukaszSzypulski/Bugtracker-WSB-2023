package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.repositories.ProjectRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    final private ProjectRepository projectRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Page<Project> findAll(Specification<Project> specification, Pageable pageable) {
        return projectRepository.findAll(specification, pageable);
    }

    public void save(Project project) {
        if (project.getDateCreated() == null) {
            project.setDateCreated(new Date());
        }
        projectRepository.save(project);
    }

    public Boolean isProjectNameUnique(Project project) {
        return projectRepository.findByName(project.getName()).isPresent();
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }


    public Long saveAndReturnId(Project project) {
        if (project.getDateCreated() == null) {
            project.setDateCreated(new Date());
        }
        projectRepository.save(project);
        return projectRepository.findById(project.getId()).get().getId();
    }

}
