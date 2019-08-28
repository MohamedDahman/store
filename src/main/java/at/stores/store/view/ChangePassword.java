package at.stores.store.view;

import at.stores.store.dao.PermissionRepository;
import at.stores.store.dao.UserRepository;
import at.stores.store.dto.PermissionDto;
import at.stores.store.entity.Permission;
import at.stores.store.entity.Provider;
import at.stores.store.entity.Users;
import at.stores.store.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class ChangePassword {

    private final Logger log = LoggerFactory.getLogger(ChangePassword.class);

    @Autowired
    private final PasswordEncoder passwordEncoder;


    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;


    public ChangePassword(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/changePass")
    public String registerUser() {
        return "changePass";
    }

    @PostMapping("/changePass")
    public String registerUser(String currPassword, String password, String passwordConfirm) {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Users> users = userRepository.findOneByLogin(userPrincipal.getUsername());

        Boolean result = passwordEncoder.matches(currPassword, users.get().getPassword());

        users.get().setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(users.get());
        return "redirect:index";
                /*
    } else {
        return "redirect:error";
    }
        return "redirect:index";
    }
*/
    }


    @GetMapping("/srchUsers")
    public String getAllUsers() {
        return "srchUsers";
    }

    @ModelAttribute("allUsers")
    public List<Users> getAllUsersAsModel() {
        return userRepository.findAll().stream().collect(Collectors.toList());
    }


    @GetMapping("/editUser")
    public String editUser(Model model, Long userId) {
        Optional<Users> user = userRepository.findById(userId);
        if (user.isPresent()) {
            model.addAttribute("editUser", user.get());
            Set<String> role = user.get().getRole();
            List<PermissionDto> permissionList = new ArrayList<>();

            List<Permission> permissionCollect = permissionRepository.findAll().stream().collect(Collectors.toList());
            for (Permission permission : permissionCollect) {
                List<String> collect = role.stream().filter(e -> e.equalsIgnoreCase(permission.getDescribe())).collect(Collectors.toList());
                PermissionDto build = null;
                if (collect.size() == 0) {
                    build = PermissionDto.builder().id(permission.getId()).state(0).describe(permission.getDescribe()).build();
                } else {
                    build = PermissionDto.builder().id(permission.getId()).state(1).describe(permission.getDescribe()).build();

                }
                permissionList.add(build);
            }
            model.addAttribute("permissionsList", permissionList);

        }


        return "editUser";
    }

    @PostMapping("/edUser")
    public String registerNewUser(HttpServletRequest request) throws IOException, ServletException {

        log.info("change User");

        String firstName = request.getParameter("firstName");
        String login = request.getParameter("login");
        String lastName = request.getParameter("lastName");
        String id = request.getParameter("id");

        String pass = "";
        Optional<Users> byId = userRepository.findById(Long.parseLong(id));
        if (byId.isPresent()) {
            pass = byId.get().getPassword();
        }
        List<Part> check = request.getParts().stream().filter(e -> e.getName().contains("check")).collect(Collectors.toList());


        Users newUser = new Users();
        newUser.setId(Long.parseLong(id));
        newUser.setLogin(login);
        newUser.setPassword(pass);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        Set<String> authorities = new HashSet<>();
        check.stream().forEach(e -> {
            authorities.add(request.getParameter(e.getName()));
        });

        newUser.setRole(authorities);

        userRepository.save(newUser);
        return "redirect:index";
    }


}
