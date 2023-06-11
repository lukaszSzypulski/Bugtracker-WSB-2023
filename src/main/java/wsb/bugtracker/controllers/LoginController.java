package wsb.bugtracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @GetMapping("/login")
    ModelAndView login() {
        return new ModelAndView("general/login");
    }

    @GetMapping("/loginError")
    public ModelAndView loginError(Model model) {
        model.addAttribute("loginError", true);
        return new ModelAndView();
    }

    @GetMapping("/logout")
    public ModelAndView logout(Model model) {
        return new ModelAndView("general/login");
    }
}
