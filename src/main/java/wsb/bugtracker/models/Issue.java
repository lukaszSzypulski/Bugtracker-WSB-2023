package wsb.bugtracker.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.Date;
import java.util.List;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Data
@Audited(targetAuditMode = NOT_AUDITED)
public class Issue {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.TODO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type = Type.TASK;

    @Column(nullable=false)
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
    private Date lastUpdated;

    @OneToMany
    private List<Comment> comments;

    private String attachment;
}
