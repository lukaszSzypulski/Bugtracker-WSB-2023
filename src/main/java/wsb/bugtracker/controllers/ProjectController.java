package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.filters.ProjectFilter;
import wsb.bugtracker.models.Mail;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.services.MailService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    final private ProjectService projectService;
    final private PersonService personService;
    final private MailService mailService;

    @GetMapping
    ModelAndView index(@ModelAttribute ProjectFilter filter, Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("projects/index");
        Page<Project> projects = projectService.findAll(filter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);
        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);
        modelAndView.addObject("filter", filter);
        return modelAndView;
    }

    @GetMapping("/create")
    ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("projects/create");

        Project newProject = new Project();
        newProject.setEnabled(true);

        modelAndView.addObject("project", newProject);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        return modelAndView;
    }

    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Project project,
                      BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/projects");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("projects/create");
            modelAndView.addObject("project", project);
            modelAndView.addObject("people", personService.findAll());
            return modelAndView;
        }

        Long aLong = projectService.saveAndReturnId(project);

        if (personService.findById(project.getCreator().getId()).isPresent()) {
            String projectUrl = "http://localhost:8080/projects/getProject/" + aLong;

            String emailAddress = personService.findById(project.getCreator().getId()).get().getEmail();
            String emailSubject = "Dodano nowe zgloszenie numer: " + aLong;
            String emailContent = "Zajmij sie nim niezwlocznie: " + "<a href='" + projectUrl + "'>" + "Link</a>";


            Mail mail = new Mail();
            mail.setRecipient(emailAddress);
            mail.setSubject(emailSubject);
            mail.setContent(emailContent);
            mailService.sendMail(mail);

        }


        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    ModelAndView delete(@PathVariable Long id) {
        System.out.println("usuwanie projektu " + id);
        projectService.delete(id);
        return new ModelAndView("redirect:/projects");
    }

    @GetMapping("/edit/{id}")
    ModelAndView editProject(@PathVariable("id") Long id) {

        ModelAndView modelAndView = new ModelAndView("projects/edit");

        if (projectService.findById(id).isPresent()) {
            Project project = projectService.findById(id).get();
            modelAndView.addObject("project", project);
        }

        return modelAndView;
    }


    @PostMapping("/edit/{id}")
    ModelAndView saveEditedProject(@PathVariable Long id, @ModelAttribute Project newProject) {

        try {
            Project oldProject = projectService.findById(newProject.getId()).get();
            oldProject.setDescription(newProject.getDescription());
            projectService.save(oldProject);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return new ModelAndView("redirect:/projects");
    }

    @GetMapping("/getProject/{id}")
    ModelAndView getProject(@PathVariable Long id) {

        ModelAndView modelAndView = new ModelAndView("projects/view");

        Project project = projectService.findById(id).get();

        modelAndView.addObject(project);

        return modelAndView;

    }
}
