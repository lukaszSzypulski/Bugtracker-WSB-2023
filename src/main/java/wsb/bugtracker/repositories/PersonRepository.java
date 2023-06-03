package wsb.bugtracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wsb.bugtracker.models.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
