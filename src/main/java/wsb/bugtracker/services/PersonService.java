package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.repositories.PersonRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PersonService {

    final private PersonRepository personRepository;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private AuthorityService authorityService;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public void save(Person person) {
        personRepository.save(person);
    }

    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    @Value("${secret.admin.username}")
    private String adminUsername;

    @Value("${secret.admin.password}")
    private String adminPass;

    public void saveAdmin() {
        String username = adminUsername;
        String password = adminPass;

        System.out.println("nazwa użytkownika administratora: " + adminUsername);

        Optional<Person> person = personRepository.findByUsername(username);

        if (person.isPresent()) {
            System.out.println("Użytkownik administracyjny już istnieje, przerywamy");
//            saveAllAuthorities(person.get());
            return;
        }

        System.out.println("Tworzymy użytkownika administracyjnego...");

        Person newPerson = new Person();
        newPerson.setUsername(username);
        newPerson.setRealName(username);

        String hashedPassword = bCryptPasswordEncoder.encode(password);
        newPerson.setPassword(hashedPassword);

        personRepository.save(newPerson);

        saveAllAuthorities(newPerson);
    }

    public void saveAllAuthorities(Person person) {
        List<Authority> authorities = authorityService.findAll();
        Set<Authority> authoritySet = new HashSet<>(authorities);

        person.setAuthorities(authoritySet);

        personRepository.save(person);
    }

    public String getProjectCreatorData(Project project) {
        return project.getCreator().getUsername();
    }
}
