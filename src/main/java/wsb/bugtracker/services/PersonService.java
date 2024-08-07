package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.repositories.AuthorityRepository;
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
    final private AuthorityRepository authorityRepository;

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

        System.out.println("Admin Login:" + adminUsername);
        System.out.println("Admin Password:" + adminPass);

        Optional<Person> person = personRepository.findByUsername(username);

        if (person.isPresent()) {
            saveAllAuthorities(person.get());
            return;
        }

        Person newPerson = new Person();
        newPerson.setUsername(username);
        newPerson.setRealName(username);

        String hashedPassword = bCryptPasswordEncoder.encode(password);
        newPerson.setPassword(hashedPassword);
        personRepository.save(newPerson);

        saveAllAuthorities(newPerson);
    }

    public void saveAllAuthorities(Person person) {
        List<Authority> authorities = authorityRepository.findAll();
        Set<Authority> authoritySet = new HashSet<>(authorities);

        person.setAuthorities(authoritySet);

        personRepository.save(person);
    }

    public Boolean isUsernameUnique(Person person) {
        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            Person user = personRepository.findByUsername(person.getUsername()).get();
            if (user.getUsername().equals(person.getUsername()) || user.getId().equals(person.getId())) {
                return true;
            }
            ;
        }
        return false;
    }

    public Object findLoggedUserId(@CurrentSecurityContext(expression = "authentication?.name") String loggedUserName) {
        if (findByUsername(loggedUserName).isEmpty()) {
            return new UsernameNotFoundException("User not found");
        }
        return findByUsername(loggedUserName).get().getId();
    }
}
