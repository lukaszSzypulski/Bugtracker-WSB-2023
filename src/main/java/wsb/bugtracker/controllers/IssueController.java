package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import wsb.bugtracker.filters.IssueFilter;
import wsb.bugtracker.filters.ProjectFilter;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.services.IssueService;
import wsb.bugtracker.services.PersonService;
import wsb.bugtracker.services.ProjectService;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/issues")
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;
    private final PersonService personService;
    private final ProjectService projectService;

    @GetMapping
    ModelAndView index(@ModelAttribute IssueFilter filter, Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("issues/index");

        Page<Issue> issues = issueService.findAll(filter.buildSpecification(), pageable);
        modelAndView.addObject("issues", issues);
        modelAndView.addObject("filter", filter);
        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);
        List<Project> projects = projectService.findAll();
        modelAndView.addObject("projects", projects);
        return modelAndView;
    }

    @GetMapping("/create")
    ModelAndView create(@ModelAttribute ProjectFilter projectFilter, Pageable pageable) {

        Issue newIssue = new Issue();
        ModelAndView modelAndView = new ModelAndView("issues/create");
        modelAndView.addObject("issue", newIssue);

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        Page<Project> projects = projectService.findAll(projectFilter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);

        return modelAndView;
    }

    @GetMapping("/delete/{id}")
    ModelAndView delete(@PathVariable Long id) {
        ModelAndView modelAndView = new ModelAndView("issues/index");
        try {
            issueService.delete(id);
        } catch (DataIntegrityViolationException e) {
            modelAndView.addObject("message", "nie udało się usunąć zgloszenia");
        }
        modelAndView.addObject("issue", issueService.findAll());
        return modelAndView;
    }

    @PostMapping("/save")
    ModelAndView save(@ModelAttribute @Valid Issue issue, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("redirect:/issues");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("issues/create");
            modelAndView.addObject("issue", issue);
            return modelAndView;
        }
        issue.setDateCreated(new Date());
        issue.setLastUpdated(new Date());
        String userLoggedName = (SecurityContextHolder.getContext().getAuthentication().getName());

        if (personService.findByUsername(userLoggedName).isPresent()) {
            issue.setCreator(personService.findByUsername(userLoggedName).get());
        }

        issueService.save(issue);
        return modelAndView;

    };

    @GetMapping("/edit/{id}")
    ModelAndView editIssue(@PathVariable("id") Long id, @ModelAttribute ProjectFilter projectFilter, Pageable pageable){

        ModelAndView modelAndView = new ModelAndView("issues/edit");

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        Page<Project> projects = projectService.findAll(projectFilter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);

        if (issueService.findById(id).isPresent()){
            Issue issue = issueService.findById(id).get();
            modelAndView.addObject("issue", issue);
        }

        return modelAndView;
    }


    @PostMapping("/edit/{id}")
    ModelAndView saveEditedIssue (@PathVariable Long id, @ModelAttribute @Valid Issue newIssue) {

        ModelAndView modelAndView = new ModelAndView("redirect:/issues");

        try{
            Issue oldIssue = issueService.findById(newIssue.getId()).get();

            oldIssue.setLastUpdated(new Date());

            issueService.save(oldIssue);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return modelAndView;
    }

    @GetMapping("/view/{id}")
    ModelAndView view(@PathVariable Long id, ProjectFilter projectFilter, Pageable pageable) {

        ModelAndView modelAndView = new ModelAndView("issues/view");

        List<Person> people = personService.findAll();
        modelAndView.addObject("people", people);

        Page<Project> projects = projectService.findAll(projectFilter.buildSpecification(), pageable);
        modelAndView.addObject("projects", projects);
        if (issueService.findById(id).isPresent()) {
            Issue issue = issueService.findById(id).get();
            modelAndView.addObject("issue", issue);
        }
        return modelAndView;
    }

}
