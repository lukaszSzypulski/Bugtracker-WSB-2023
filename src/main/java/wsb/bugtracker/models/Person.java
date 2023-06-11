package wsb.bugtracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Entity
@Data
@RequiredArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String realName;

    @Email
    private String email;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "person_authorities",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id"))
    private Set<Authority> authorities;

    public Person(String adminUsername, String encode, String adminUsername1) {
        this.username = adminUsername;
        this.password = encode;
        this.realName = adminUsername1;
    }
}
