package wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.repositories.AuthorityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;


    public List<Authority> findAll() {
        return authorityRepository.findAll();
    }

    public void save(Authority authority) {
        authorityRepository.save(authority);
    }
}
