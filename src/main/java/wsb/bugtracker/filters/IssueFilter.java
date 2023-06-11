package wsb.bugtracker.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private String status;
    private String issueName;


    public Specification<Issue> buildSpecification() {
        return Specification.allOf(
                equalTo("project", project),
                equalTo("assignee", assignee)
//                enumEqual("status", status)
//                equalTo("status", status)
        );
    }

    private Specification<Issue> equalTo(String property, Object value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, builder) -> builder.equal(root.get(property), value);
    }

    private Specification<Issue> enumEqual(String property, Object value) {
        if (value == null) {
            return Specification.where(null);
        }
        return (root, query, builder) -> builder.equal(root.get(property), Status.valueOf(value.toString()));
        //TODO: need to finish filter by STATUS
    }


    public String toQueryString(Integer page, Sort sort) {
        return "page=" + page +
                "&sort=" + toSortString(sort) +
                (project != null ? "&project=" + project : "") +
                (assignee != null ? "&assignee=" + assignee.getId() : "");
    }

    public String toSortString(Sort sort) {
        Sort.Order order = sort.getOrderFor("name");
        String sortString = "";
        if (order != null) {
            sortString += "name," + order.getDirection();
        }
        return sortString;
    }

    public Sort findNextSorting(Sort currentSorting) {
        Sort.Direction currentDirection = currentSorting.getOrderFor("name") != null ? currentSorting.getOrderFor("name").getDirection() : null;

        if (currentDirection == null) {
            return Sort.by("name").ascending();
        } else if (currentDirection.isAscending()) {
            return Sort.by("name").descending();
        } else {
            return Sort.unsorted();
        }

    }

}
