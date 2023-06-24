package wsb.bugtracker.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import wsb.bugtracker.repositories.ProjectRepository;
import wsb.bugtracker.services.ProjectService;

import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {


    private ProjectService projectService;

    @Mock
    ProjectRepository projectRepository;


    @BeforeEach
    void setUp() {
        this.projectService
                = new ProjectService(this.projectRepository);
    }

    //    @InjectMocks
//    Project project = new Project();
    @Test
    void isUnique() {
        projectService.findAll();
        verify(projectRepository).findAll();


        //  when(projectService.findAll(1L).get().getName()).thenReturn("DESTA");


    }

    @Test
    void dateValidation() {

//        ReflectionTestUtils.setField(projectService, "adminUsername", adminUsername);
//
//        projectService.save(project);
//        verify()

    }


//    public void save(Project project) {
//        if (project.getDateCreated() == null) {
//            project.setDateCreated(new Date());
//        }
//        projectRepository.save(project);
//    }

//    public Boolean isProjectNameUnique(Project project) {
//        if (projectRepository.findByName(project.getName()).isPresent()) {
//            Project oldProject = projectRepository.findByName(project.getName()).get();
//            if (oldProject.getName().equals(project.getName()) || oldProject.getId().equals(project.getId())) {
//                return true;
//            }
//            ;
//        }
//        return false;

}
