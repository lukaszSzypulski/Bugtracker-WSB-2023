package wsb.bugtracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import wsb.bugtracker.annotation.ValidPasswords;

import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
@ValidPasswords
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @NotEmpty(message = "{username.notEmpty}")
    private String username;

    @NotNull
    @NotEmpty(message = "{password.notEmpty}")
    private String password;

    @Transient
    String repeatedPassword;

    @NotNull
    @NotEmpty(message = "{username.realName.notEmpty}")
    private String realName;

    @Email
    private String email;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "person_authorities",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authorities;

    public Person(String adminUsername, String encode, String realName) {
        this.username = adminUsername;
        this.password = encode;
        this.realName = realName;
    }
}
