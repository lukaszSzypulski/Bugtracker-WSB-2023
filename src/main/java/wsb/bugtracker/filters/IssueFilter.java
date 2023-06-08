package wsb.bugtracker.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import wsb.bugtracker.models.Issue;
import wsb.bugtracker.models.Project;
import wsb.bugtracker.models.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueFilter {


    private String name;
    private Issue assignee;
    private String globalSearch;
    private Status status;
    private Project project;


    public Specification<Project> buildSpecification() {
        return Specification.anyOf(
                ilike("type", globalSearch)
        );
    }

    private Specification<Project> equalTo(String property, Object value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> builder.equal(root.get(property), value);
    }

    private Specification<Project> ilike(String property, String value) {
        if (value == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> builder.like(builder.lower(root.get(property)), "%" + value.toLowerCase() + "%");
    }

}
