package pl.wsb.bugtracker.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wsb.bugtracker.models.Person;
import pl.wsb.bugtracker.repositories.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonService {

    final private PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public void save(Person person){
        personRepository.save(person);
    }

    public Optional <Person> findById(Long id){return personRepository.findById(id);}
}
