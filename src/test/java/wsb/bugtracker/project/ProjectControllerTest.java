package wsb.bugtracker.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import wsb.bugtracker.controllers.ProjectController;
import wsb.bugtracker.services.IssueService;
import wsb.bugtracker.services.MailService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ProjectControllerTest {

    @MockBean
    private ProjectService projectService;

    @MockBean
    private PersonService personService;

    @MockBean
    private MailService mailService;

    @MockBean
    private IssueService issueService;

    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectController projectController;


    @Test
    public void controllerExists() throws Exception {
        assertThat(projectController).isNotNull();
    }


    @Test
    void newProjectShouldNotBeCreated() throws Exception {
        this.mockMvc.perform(get("/create"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
