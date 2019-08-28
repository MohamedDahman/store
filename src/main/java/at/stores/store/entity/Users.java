package at.stores.store.entity;


import at.stores.store.config.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;


    @Column(name = "first_name", nullable = false, length = 50)
    @Size(min = 1, max = 50)
    private String firstName;


    @Column(name = "last_name", nullable = false, length = 50)
    @Size(min = 1, max = 50)
    private String lastName;

    @Column
    @Size(min = 1, max = 255)
    private String password;


    @Email
    @Size(min = 5, max = 100)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> role = new HashSet<>();

    public Users(@NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String login) {
        this.login = login;
    }

/*
    public Users(Long id, @NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 50) String login, @Size(min = 1, max = 50) String firstName, @Size(min = 1, max = 50) String lastName, @Size(min = 1, max = 255) String password) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;

    }
*/


}
