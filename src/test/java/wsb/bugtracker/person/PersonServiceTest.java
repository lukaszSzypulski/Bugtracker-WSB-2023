package wsb.bugtracker.person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.repositories.AuthorityRepository;
import wsb.bugtracker.repositories.PersonRepository;
import wsb.bugtracker.services.PersonService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private final String adminUsername = "TesterMockito";
    private final String adminPass = "Test123";

    @InjectMocks
    PersonService personService;

    @Mock
    PersonRepository personRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AuthorityRepository authorityRepository;

    @Test
    void adminCreatedIfNotExists() {

        ReflectionTestUtils.setField(personService, "adminUsername", adminUsername);
        ReflectionTestUtils.setField(personService, "adminPass", adminPass);

        personService.saveAdmin();

        verify(personRepository, times(1)).findByUsername(adminUsername);
        verify(bCryptPasswordEncoder, times(1)).encode(adminPass);
        verify(personRepository, times(2)).save(any(Person.class));

    }

    @Test
    void adminNotCreatedWhenExists() {
        ReflectionTestUtils.setField(personService, "adminUsername", adminUsername);
        ReflectionTestUtils.setField(personService, "adminPass", adminPass);

        Optional<Person> optionalPerson = Optional.of(new Person("Leszek15", "NiezwykleTrudneHaslo15", "Leszek Nowak"));
        when(personRepository.findByUsername(adminUsername)).thenReturn(optionalPerson);

        personService.saveAdmin();

        verify(personRepository, times(1)).findByUsername(adminUsername);
        verify(bCryptPasswordEncoder, times(0)).encode(adminPass);
    }

}
