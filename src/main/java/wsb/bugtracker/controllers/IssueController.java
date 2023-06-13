package wsb.bugtracker.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    ModelAndView save(@ModelAttribute @Valid Issue issue, BindingResult bindingResult) throws IOException {

        ModelAndView modelAndView = new ModelAndView("redirect:/issues");

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("issues/create");
            modelAndView.addObject("issue", issue);
            return modelAndView;
        }

        if (!issue.getAttachment().isEmpty()) {
            //TODO: Try catch with FileOutputStream
            File file = new File("src/main/resources/static/" + issue.getAttachment());
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.close();
        }
        issue.setAttachment(issue.getAttachment());
        issue.setDateCreated(new Date());
        issue.setLastUpdated(new Date());
        String userLoggedName = (SecurityContextHolder.getContext().getAuthentication().getName());

        if (personService.findByUsername(userLoggedName).isPresent()) {
            issue.setCreator(personService.findByUsername(userLoggedName).get());
        }

        issueService.save(issue);
        return modelAndView;

    }

    @GetMapping("/edit/{id}")
    ModelAndView editIssue(@PathVariable("id") Long id, @ModelAttribute ProjectFilter projectFilter, Pageable pageable) {

        ModelAndView modelAndView = new ModelAndView("issues/edit");

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


    @PostMapping("/edit/{id}")
    ModelAndView saveEditedIssue(@PathVariable Long id, @ModelAttribute @Valid Issue newIssue) {

        ModelAndView modelAndView = new ModelAndView("redirect:/issues");

        try {
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

    @GetMapping("/deleteFile/{id}")
    public ModelAndView deleteFile(@PathVariable Long id) {

        Issue issueId = issueService.findById(id).get();
        ModelAndView modelAndView = new ModelAndView("redirect:/issues/edit/" + issueId.getId());

        //TODO cleanup
        File file = new File("src/main/resources/static/" + issueId.getAttachment());
        file.delete();
        if (issueService.findById(id).isPresent()) {
            issueService.findByIdAndSetAttachment(id);
        }

        return modelAndView;

    }

    @GetMapping("/download/{id}")
    public ResponseEntity downloadFile(@PathVariable Long id) throws IOException {
        Issue ticket = issueService.findById(id).get();

        File file = new File("src/main/resources/static/" + ticket.getAttachment());
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource
                (Files.readAllBytes(path));

        return ResponseEntity.ok().headers(this.headers(ticket.getAttachment()))
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType
                        ("application/octet-stream")).body(resource);
    }

//        Issue ticket = issueService.findById(id).get();
//        File file = new File("src/main/resources/static/" + ticket.getAttachment() );
//        System.out.println(file.isFile());
//
//        HttpHeaders header = new HttpHeaders();
//        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=img.jpg");
//        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        header.add("Pragma", "no-cache");
//        header.add("Expires", "0");
//
//        Path path = Paths.get(file.getAbsolutePath());
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//
//        return ResponseEntity.ok()
//                .headers(header)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(resource);

//    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws FileNotFoundException {
//        try {
//            Issue ticket = issueService.findById(id).get();
//            String fileName = Paths.get("src/main/resources/static/").resolve(ticket.getAttachment()).toString();
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            String headerKey = "Content-Disposition";
//            String headerValue = String.format("attachment; filename=\"%s\"", fileName);
//            response.setHeader(headerKey, headerValue);
//            FileInputStream inputStream;
//            try {
//                inputStream = new FileInputStream(fileName);
//                try {
//                    int c;
//                    while ((c = inputStream.read()) != -1) {
//                        response.getWriter().write(c);
//                    }
//                } finally {
//                    if (inputStream != null)
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    response.getWriter().close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }} catch (Exception e) {
//            throw new RuntimeException(e);
//        }}


    //TODO: in model edit add if issue.getAtt is null do not show btn
//        Issue issue = issueService.findById(id).get();
//        return new FileInputStream("src/main/resources/static/" + issue.getAttachment());
//    }

    private HttpHeaders headers(String name) {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + name);
        header.add("Cache-Control", "no-cache, no-store,"
                + " must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;

    }
}

