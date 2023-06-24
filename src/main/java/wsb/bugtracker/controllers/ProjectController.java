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
import wsb.bugtracker.models.MailDataDTO;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
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
    private MailDataDTO mail = new MailDataDTO();

    @GetMapping
    ModelAndView index(@ModelAttribute ProjectFilter filter, Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("projects/index");
        return getModelAndView(filter, pageable, modelAndView);
    }

    @Secured("ROLE_CREATE_PROJECT")
    @GetMapping("/create")
    ModelAndView create(Project project) {
        ModelAndView modelAndView = new ModelAndView("projects/create");

        modelAndView.addObject("project", project);

        modelAndView.addObject("people", personService.findAll());

        return modelAndView;
    }

    @Secured("ROLE_CREATE_PROJECT")
    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Project project, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/projects");
        modelAndView.addObject("project", project);
        modelAndView.addObject("people", personService.findAll());

        if (bindingResult.hasErrors() || projectService.isProjectNameUnique(project)) {
            modelAndView.setViewName("projects/create");
            if (projectService.isProjectNameUnique(project)) {
                bindingResult.rejectValue("name", "project.name.unique");
            }
            return modelAndView;
        }

        if (personService.findById(project.getCreator().getId()).isPresent()) {
            Long projectId = projectService.saveAndReturnId(project);
            mailSender(project, projectId);
        }

        projectService.save(project);
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

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("projects/edit");
            modelAndView.addObject("project", newProject);
            return modelAndView;
        }

        if (projectService.findById(newProject.getId()).isPresent()) {
            Project oldProject = projectService.findById(newProject.getId()).get();
            oldProject.setDescription(newProject.getDescription());
            oldProject.setName(newProject.getName());
            projectService.save(oldProject);
        }

        return modelAndView;
    }

    @GetMapping("/getProject/{id}")
    ModelAndView getProject(@PathVariable Long id) {

        ModelAndView modelAndView = new ModelAndView("projects/view");

        if (projectService.findById(id).isPresent()) {
            modelAndView.addObject("project", projectService.findById(id).get());
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

        return getModelAndView(filter, pageable, modelAndView);
    }

    private ModelAndView getModelAndView(@ModelAttribute ProjectFilter filter, Pageable pageable, ModelAndView modelAndView) {
        Page<Project> projects = projectService.findAll(filter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);
        modelAndView.addObject("filter", filter);

        return modelAndView;
    }

    private void mailSender(Project project, Long projectId) {
        String projectUrl = "http://localhost:8080/projects/getProject/" + projectId;
        String emailAddress = personService.findById(project.getCreator().getId()).get().getEmail();
        String emailSubject = "Added new project number: " + projectId;
        String emailContent = "Deal with it immediately: " + "<a href='" + projectUrl + "'>" + "Link</a>";

        mail.setRecipient(emailAddress);
        mail.setSubject(emailSubject);
        mail.setContent(emailContent);
        mailService.sendMail(mail);
    }
}
