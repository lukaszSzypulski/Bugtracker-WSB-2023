package wsb.bugtracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.AuthorityName;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(AuthorityName name);

}
