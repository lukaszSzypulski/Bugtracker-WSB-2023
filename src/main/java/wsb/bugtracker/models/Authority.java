package wsb.bugtracker.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Authority {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

}
