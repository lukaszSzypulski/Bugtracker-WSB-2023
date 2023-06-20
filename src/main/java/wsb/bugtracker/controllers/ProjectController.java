package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.filters.ProjectFilter;
import wsb.bugtracker.models.Mail;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.services.IssueService;
import wsb.bugtracker.services.MailService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    final private ProjectService projectService;
    final private PersonService personService;
    final private MailService mailService;
    final private IssueService issueService;

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

    @Secured("ROLE_CREATE_PROJECT")
    @GetMapping("/create")
    ModelAndView create() {
        ModelAndView modelAndView = new ModelAndView("projects/create");

        Project newProject = new Project();
        newProject.setEnabled(true);

        modelAndView.addObject("project", newProject);

        modelAndView.addObject("people", personService.findAll());

        return modelAndView;
    }

    @Secured("ROLE_CREATE_PROJECT")
    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Project project, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/projects");

        if (bindingResult.hasErrors() || projectService.isProjectNameUnique(project)) {
            modelAndView.setViewName("projects/create");
            modelAndView.addObject("project", project);
            modelAndView.addObject("people", personService.findAll());
            if (projectService.isProjectNameUnique(project)) {
                bindingResult.rejectValue("name", "project.name.unique");
            }
            return modelAndView;
        }

        if (personService.findById(project.getCreator().getId()).isPresent()) {
            Long projectId = projectService.saveAndReturnId(project);
            String projectUrl = "http://localhost:8080/projects/getProject/" + projectId;

            String emailAddress = personService.findById(project.getCreator().getId()).get().getEmail();
            String emailSubject = "Dodano nowe zgloszenie numer: " + projectId;
            String emailContent = "Zajmij sie nim niezwlocznie: " + "<a href='" + projectUrl + "'>" + "Link</a>";


            Mail mail = new Mail();
            mail.setRecipient(emailAddress);
            mail.setSubject(emailSubject);
            mail.setContent(emailContent);
            mailService.sendMail(mail);

        }
        return modelAndView;
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
    ModelAndView saveEditedProject(@ModelAttribute @Valid Project newProject, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/projects");


        if (bindingResult.hasErrors() || projectService.isProjectNameUnique(newProject)) {
            modelAndView.setViewName("projects/edit");
            modelAndView.addObject("project", newProject);
            if (projectService.isProjectNameUnique(newProject)) {
                bindingResult.rejectValue("name", "project.name.unique");
            }
            return modelAndView;
        }

        if (projectService.findById(newProject.getId()).isPresent()) {
            Project oldProject = projectService.findById(newProject.getId()).get();
            oldProject.setDescription(newProject.getDescription());
            oldProject.setName(newProject.getName());
            projectService.save(oldProject);
        }

        return new ModelAndView("redirect:/projects");
    }

    @GetMapping("/getProject/{id}")
    ModelAndView getProject(@PathVariable Long id) {

        ModelAndView modelAndView = new ModelAndView("projects/view");

        if (projectService.findById(id).isPresent()) {
            modelAndView.addObject(projectService.findById(id).get());
        }

        return modelAndView;

    }

    @GetMapping("/delete/{id}")
    ModelAndView delete(@PathVariable Long id, @ModelAttribute ProjectFilter filter, Pageable pageable) {

        ModelAndView modelAndView = new ModelAndView("projects/index");
        try {
            projectService.delete(id);
        } catch (DataIntegrityViolationException e) {
            modelAndView.addObject("message", "Nie udało się usunąć projektu ponieważ jest używany w innych miejsach systemu");
        }

        Page<Project> projects = projectService.findAll(filter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);
        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);
        modelAndView.addObject("filter", filter);

        return modelAndView;
    }
}
