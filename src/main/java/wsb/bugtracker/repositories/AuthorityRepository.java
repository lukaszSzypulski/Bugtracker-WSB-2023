package wsb.bugtracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.AuthorityName;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(AuthorityName name);
}
