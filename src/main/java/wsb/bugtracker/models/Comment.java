package wsb.bugtracker.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private Date dateCreated;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private Person person;

    private String content;
}
