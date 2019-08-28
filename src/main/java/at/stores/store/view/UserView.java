package at.stores.store.view;

import at.stores.store.dao.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserView {


    private final Logger log = LoggerFactory.getLogger(General.class);

    @Autowired
    UserRepository userRepository;

    @GetMapping("/login")
    public String loginUser() {
        return "login";
    }



}
