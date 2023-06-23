package wsb.bugtracker.project;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;


@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {


    @Mock
    PersonService personService;

    @InjectMocks
    ProjectService projectService;

}
