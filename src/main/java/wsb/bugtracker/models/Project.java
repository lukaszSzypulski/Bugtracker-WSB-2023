package wsb.bugtracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @NotEmpty(message = "{project.name.notEmpty}")
    @Size(min = 3, max = 10)
    private String name;

    @NotNull
    private Boolean enabled = true;

    @NotNull
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateCreated;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Person creator;
}
