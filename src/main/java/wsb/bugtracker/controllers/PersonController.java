package wsb.bugtracker.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.models.Authority;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.services.AuthorityService;
import wsb.bugtracker.services.PersonService;

import java.util.List;

@Controller
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    final private PersonService personService;
    final private AuthorityService authorityService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Secured("ROLE_LIST_USER")
    @GetMapping()
    ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("person/index");
        modelAndView.addObject("people", personService.findAll());
        return modelAndView;
    }

    @Secured("ROLE_CREATE_USER")
    @GetMapping("/create")
    ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("person/create");

        List<Authority> authoritiesList = authorityService.findAll();

        modelAndView.addObject("authoritiesList", authoritiesList);

        modelAndView.addObject("person", new Person());

        return modelAndView;
    }

    @Secured("ROLE_CREATE_USER")
    @PostMapping("/save")
    ModelAndView save(@Valid @ModelAttribute Person person,
                      BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        List<Authority> authoritiesList = authorityService.findAll();

        modelAndView.addObject("authoritiesList", authoritiesList);

        if (bindingResult.hasErrors() || personService.isUsernameUnique(person)) {
            modelAndView.setViewName("person/create");
            if (personService.isUsernameUnique(person)) {
                bindingResult.rejectValue("username", "person.name.unique");
            }
            return modelAndView;
        }

        String hashedPassword = bCryptPasswordEncoder.encode(person.getPassword());
        person.setPassword(hashedPassword);
        personService.save(person);

        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    ModelAndView editPerson(@PathVariable Long id) {

        ModelAndView modelAndView = new ModelAndView("person/edit");

        List<Authority> authoritiesList = authorityService.findAll();

        modelAndView.addObject("authoritiesList", authoritiesList);


        if (personService.findById(id).isPresent()) {
            Person person = personService.findById(id).get();
            modelAndView.addObject("person", person);
        }

        return modelAndView;
    }


    @PostMapping("/edit/{id}")
    ModelAndView saveEditedPerson(@PathVariable Long id, @ModelAttribute @Valid Person newPerson, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        List<Authority> authoritiesList = authorityService.findAll();

        modelAndView.addObject("authoritiesList", authoritiesList);

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("person/edit");
            return modelAndView;
        }

        if (personService.findById(newPerson.getId()).isPresent()) {
            Person oldPerson = personService.findById(newPerson.getId()).get();
            oldPerson.setRealName(newPerson.getRealName());

            String hashedPassword = bCryptPasswordEncoder.encode(newPerson.getPassword());
            oldPerson.setPassword(hashedPassword);
            oldPerson.setAuthorities(newPerson.getAuthorities());
            oldPerson.setEmail(newPerson.getEmail());
            personService.save(oldPerson);
        }
        return modelAndView;
    }

    @Secured("ROLE_LIST_USER")
    @GetMapping("/view/{id}")
    ModelAndView view(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("person/view");

        List<Authority> authoritiesList = authorityService.findAll();

        modelAndView.addObject("authoritiesList", authoritiesList);

        if (personService.findById(id).isPresent()) {
            Person person = personService.findById(id).get();
            modelAndView.addObject("person", person);
        }

        return modelAndView;
    }

    @Secured("ROLE_DELETE_USER")
    @GetMapping("/delete/{id}")
    ModelAndView delete(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("person/index");
        try {
            personService.delete(id);
        } catch (DataIntegrityViolationException e) {
            modelAndView.addObject("message", "Nie udało się usunąć użytkownika ponieważ jest używany w innych miejscach systemu");
        }
        modelAndView.addObject("people", personService.findAll());
        return modelAndView;
    }

}
