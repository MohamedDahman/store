package at.stores.store.initialise;

import at.stores.store.dao.PermissionRepository;
import at.stores.store.dao.UserRepository;
import at.stores.store.entity.Permission;
import at.stores.store.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Configuration
public class ApplicationInitialiser {

    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    UserRepository userRepository;

    @Bean
    ApplicationRunner intialiseData() {
        return args -> {
            Optional<Users> admin = userRepository.findOneByLogin("admin");
            if (!admin.isPresent()) {

                Users user = Users.builder()
                        .email("admin@example.com")
                        .firstName("admin")
                        .id(0L)
                        .lastName("admin")
                        .login("admin")
                        .password("$2a$10$S8g9x3ud52S0hsIjNGdZneZgzn0V.csgfxPkSVzUmMx7L58LKHMxm")
                        .role(new HashSet<>(Arrays.asList("admin")))
                        .build();
                userRepository.save(user);

            }

            admin = userRepository.findOneByLogin("mhed");
            if (!admin.isPresent()) {

                Users user = Users.builder()
                        .email("admin@example.com")
                        .firstName("mhed")
                        .id(0L)
                        .lastName("mhed")
                        .login("mhed")
                        .password("$2a$10$S8g9x3ud52S0hsIjNGdZneZgzn0V.csgfxPkSVzUmMx7L58LKHMxm")
                        .role(new HashSet<>(Arrays.asList("admin")))
                        .build();
                userRepository.save(user);

            }

            String permissions =
                    "ADMIN,CATEGORY,CLASSES,UNITS,PROVIDER,MATERIAL,PURCHASES,MANAGE PURCHASES,SALES,MANAGE SALES,USERS,PERMISSIONS,CHANGE PASSWORD";
            Arrays.stream(permissions.split(",")).forEach(
                    e -> {
                        long count = permissionRepository.findAll().stream().filter(permission -> permission.getDescribe().equalsIgnoreCase(e)).count();
                        if (count == 0L) {
                            permissionRepository.save(new Permission(e));
                        }
                    }
            );


        };
    }
}