package wsb.bugtracker.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.repositories.PersonRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class CustomerDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;


    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByUsername(username).orElse(null);

        if (person == null) {
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> grantedAuthorities = findAuthorities(person);

        return new User(username, person.getPassword(), grantedAuthorities);
    }

    private Set<GrantedAuthority> findAuthorities(Person person) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Authority authority : person.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName().name());

            authorities.add(grantedAuthority);
        }

        return authorities;
    }
}
