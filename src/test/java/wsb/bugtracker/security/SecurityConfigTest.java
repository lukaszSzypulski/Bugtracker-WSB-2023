package wsb.bugtracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import wsb.bugtracker.controllers.ProjectController;
import wsb.bugtracker.services.IssueService;
import wsb.bugtracker.services.MailService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(ProjectController.class)
public class SecurityConfigTest {

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
    void authenticationWithPassword() throws Exception {
        mockMvc.perform(formLogin().password("TestPassword"))
                .andExpect(unauthenticated());
    }

    @Test
    void authenticationWithLogin() throws Exception {
        mockMvc.perform(formLogin().user("PiesekLeszek"))
                .andExpect(unauthenticated());
    }

    @Test
    void authenticationRedirect() throws Exception {
        mockMvc
                .perform(formLogin().user("admin")
                        .password("test"))
                .andExpect(redirectedUrlTemplate("/login?error"));
    }

    @WithMockUser(username = "admin", password = "test")
    @Test
    void authenticationWithCorrectCredentials() throws Exception {
        mockMvc
                .perform(formLogin().user("admin").password("test"))
                .andExpect(authenticated().withUsername("admin"));
    }
}
