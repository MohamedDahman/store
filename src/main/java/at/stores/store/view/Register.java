package at.stores.store.view;

import at.stores.store.dao.PermissionRepository;
import at.stores.store.dao.UserRepository;
import at.stores.store.entity.Permission;
import at.stores.store.entity.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class Register {
    private final Logger log = LoggerFactory.getLogger(Register.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @ModelAttribute("permissions")
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll().stream().collect(Collectors.toList());
    }


    @GetMapping("/register")
    public String registerUser() {

        return "register";
    }


    @PostMapping("/register")
    public String registerNewUser(HttpServletRequest request) throws IOException, ServletException {

        log.info("Register New Users");

        String firstName = request.getParameter("firstName");
        String login = request.getParameter("login");
        String lastName = request.getParameter("lastName");
        String pass = request.getParameter("password");


        List<Part> check = request.getParts().stream().filter(e -> e.getName().contains("check")).collect(Collectors.toList());
        check.stream().forEach(e -> System.out.println(e.getName()));


        long existLogin = userRepository.findAll().stream().filter(e -> e.getLogin().equalsIgnoreCase(login)).count();
        if (existLogin == 0L) {
            Users newUser = new Users();
            newUser.setLogin(login);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);

            newUser.setPassword(new BCryptPasswordEncoder().encode(pass));
            Set<String> authorities = new HashSet<>();
            check.stream().forEach(e -> {
                authorities.add(request.getParameter(e.getName()));
            });

            newUser.setRole(authorities);

            userRepository.save(newUser);
        } else {
            return "redirect:error";
        }
        return "redirect:index";
    }

}
