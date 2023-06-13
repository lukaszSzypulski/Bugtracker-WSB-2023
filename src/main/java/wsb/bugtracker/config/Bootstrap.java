package wsb.bugtracker.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.AuthorityName;
import wsb.bugtracker.repositories.AuthorityRepository;
import wsb.bugtracker.services.PersonService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class Bootstrap implements InitializingBean {

    private final PersonService personService;
    private final AuthorityRepository authorityRepository;


    @Override
    public void afterPropertiesSet() throws Exception {
        saveMissingAuthorities();
        personService.saveAdmin();
    }

    void saveMissingAuthorities() {
        for (AuthorityName authorityName : AuthorityName.values()) {
            Optional<Authority> authority = authorityRepository.findByName(authorityName);

            if (authority.isPresent()) {
                continue;
            }

            Authority newAuthority = new Authority();
            newAuthority.setName(authorityName);

            authorityRepository.save(newAuthority);
        }

    }

}
