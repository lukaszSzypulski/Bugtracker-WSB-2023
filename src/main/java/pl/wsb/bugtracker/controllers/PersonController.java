package pl.wsb.bugtracker.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.wsb.bugtracker.models.Person;
import pl.wsb.bugtracker.services.PersonService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/person")
@RequiredArgsConstructor
public class PersonController {

    final private PersonService personService;

    @GetMapping
    ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("person/index");
        modelAndView.addObject("people", personService.findAll());
        return modelAndView;
    }


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


    @GetMapping("/create")
    ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("person/create");

        Person newPerson = new Person();

        modelAndView.addObject("person", newPerson);

        return modelAndView;
    }

    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Person person,
                      BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("person/create");
            return modelAndView;
        }

        personService.save(person);

        return modelAndView;
    }

    @GetMapping("/edit/{id}")
    ModelAndView editPerson(@PathVariable("id") Long id){

        ModelAndView modelAndView = new ModelAndView("person/edit");

      if (personService.findById(id).isPresent()){
          Person person = personService.findById(id).get();
          modelAndView.addObject("person", person);
      }

        return modelAndView;
    }


    @PostMapping("/edit/{id}")
    ModelAndView saveEditedPerson (@PathVariable Long id, @ModelAttribute @Valid Person newPerson) {

        ModelAndView modelAndView = new ModelAndView("redirect:/person");

        try{
            Person oldPerson = personService.findById(newPerson.getId()).get();
            oldPerson.setRealName(newPerson.getRealName());
            oldPerson.setPassword(newPerson.getPassword());
            oldPerson.setAuthorities(newPerson.getAuthorities());
            oldPerson.setEmail(newPerson.getEmail());
            personService.save(oldPerson);
        }
        catch (NoSuchElementException e){
            e.printStackTrace();
        }

        return modelAndView;
    }
}
