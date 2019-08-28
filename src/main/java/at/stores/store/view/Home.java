package at.stores.store.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Controller
@SessionAttributes("IsLog")
@ControllerAdvice
public class Home {

    @GetMapping("/")
    String page() {
        return "index";
    }

    @GetMapping("/index")
    String pageIndex() {
        return "index";
    }

    @ModelAttribute("IsLog")
    public String getIsLog() {
        if (SecurityContextHolder.getContext().getAuthentication().getName().
                equals("anonymousUser")) {
            return  "NotLoged";
        }
        else
        {
            return "Loged";
        }
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/index";
    }



    @ModelAttribute("localDate")
    public LocalDate getLocalDate() {
        return LocalDate.now();
    }

    @ModelAttribute("currentUser")
    public String getGetCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
