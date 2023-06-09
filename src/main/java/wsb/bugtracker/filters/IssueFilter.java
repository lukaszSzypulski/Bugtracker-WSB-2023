package wsb.bugtracker.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Person;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.models.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueFilter {

    private Project project;
    private Person assignee;
    private Status status;


    public Specification<Issue> buildSpecification() {
        return Specification.allOf(
                equalTo("assignee", assignee)
//                ilike("name", name),
//                equalTo("creator", creator)
        );
    }

    private Specification<Issue> equalTo(String assignee, Object value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> builder.equal(root.get(assignee), value);
    }

    private Specification<Issue> contains(String property, Object value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> builder.equal(root.get(property), value);
    }

    private Specification<Issue> ilike(String property, String value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> builder.like(builder.lower(root.get(property)), "%" + value.toLowerCase() + "%");
    }

}
