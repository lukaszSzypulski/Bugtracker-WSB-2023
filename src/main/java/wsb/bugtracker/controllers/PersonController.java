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
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    final private PersonService personService;
    final private AuthorityService authorityService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Secured("ROLE_USERS_TAB")
    @GetMapping()
    ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("person/index");
        modelAndView.addObject("people", personService.findAll());
        return modelAndView;
    }

    @Secured("ROLE_DELETE_USER")
    @GetMapping("/delete/{id}")
    ModelAndView delete(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("person/index");
        try {
            personService.delete(id);
        } catch (DataIntegrityViolationException e) {
            modelAndView.addObject("message", "nie udało się usunąć użytkownika ponieważ jest używany w innych miejscach systemu");
        }
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
    ModelAndView save(@ModelAttribute @Valid Person person,
                      BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("person/create");
            return modelAndView;
        }

        String hashedPassword = bCryptPasswordEncoder.encode(person.getPassword());
        person.setPassword(hashedPassword);
        personService.save(person);

        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    ModelAndView editPerson(@PathVariable("id") @ModelAttribute Long id) {

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
    ModelAndView saveEditedPerson(@PathVariable Long id, @ModelAttribute @Valid Person newPerson) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        try {
            Person oldPerson = personService.findById(newPerson.getId()).get();
            oldPerson.setRealName(newPerson.getRealName());

            String hashedPassword = bCryptPasswordEncoder.encode(newPerson.getPassword());
            oldPerson.setPassword(hashedPassword);
            oldPerson.setAuthorities(newPerson.getAuthorities());
            oldPerson.setEmail(newPerson.getEmail());
            personService.save(oldPerson);

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return modelAndView;
    }

    @Secured("ROLE_USERS_TAB")
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

}
