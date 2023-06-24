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

    public Optional<Project> findByName(String name) {
        return projectRepository.findByName(name);
    }


    public void save(Project project) {
        if (project.getDateCreated() == null) {
            project.setDateCreated(new Date());
        }
        if (project.getEnabled() == null) {
            project.setEnabled(true);
        }
        projectRepository.save(project);
    }

    public Boolean isProjectNameUnique(Project project) {
        if (projectRepository.findByName(project.getName()).isPresent()) {
            Project oldProject = projectRepository.findByName(project.getName()).get();
            if (oldProject.getName().equals(project.getName()) || oldProject.getId().equals(project.getId())) {
                return true;
            }
            ;
        }
        return false;
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
