package at.stores.store.util;

import at.stores.store.security.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityUtils {


    public SecurityUtils() {
    }

    public static boolean isAllowed(UserPrincipal user, String role) {
        boolean isAllowed = false;
        if (user != null) {
            List<String> list = new ArrayList<>();
            user.getAuthorities().stream().forEach(e -> list.add(((GrantedAuthority) e).getAuthority()));
            if ((list.stream().filter(e -> e.equalsIgnoreCase(role)).count() == 1)||
                 (list.stream().filter(e -> e.equalsIgnoreCase("admin")).count() == 1)) {
                isAllowed = true;
            }

        }
        return isAllowed;
    }
}
