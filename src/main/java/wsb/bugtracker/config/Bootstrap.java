package wsb.bugtracker.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.services.PersonService;

@Service
public class Bootstrap implements InitializingBean {

    private final PersonService personService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //  private AuthorityService authorityService;
//    private AuthorityRepository authorityRepository;


    @Value("${secret.admin.username}")
    private String adminUsername;

    @Value("${secret.admin.password}")
    private String adminPassword;

    public Bootstrap(PersonService personService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.personService = personService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void afterPropertiesSet() {
        createDefaultUser();
    }

    private void createDefaultUser() {
        if (personService.findByUsername(adminUsername).isEmpty()) {
            personService.save(new Person(adminUsername, bCryptPasswordEncoder.encode(adminPassword), adminUsername));
        }
    }
}

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("Start");
//
//        saveMissingAuthorities();
//        personService.saveAdmin();
//    }
//
//    void saveMissingAuthorities() {
//        for (AuthorityName authorityName : AuthorityName.values()) {
//            Optional<Authority> authority = authorityRepository.findByName(authorityName);
//
//            if (authority.isPresent()) {
//                continue;
//            }
//
//            Authority newAuthority = new Authority();
//            newAuthority.setName(authorityName);
//
//            authorityRepository.save(newAuthority);
//        }
//
//    }
//
//
//}
