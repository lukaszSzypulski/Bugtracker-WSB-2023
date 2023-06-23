package wsb.bugtracker.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.Date;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@Audited(targetAuditMode = NOT_AUDITED)
public class Issue {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.TODO;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Type type = Type.TASK;

    @NotNull
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Person creator;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Person assignee;

    @Column(nullable = false)
    private Date dateCreated;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

}
