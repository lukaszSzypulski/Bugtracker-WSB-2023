package pl.wsb.bugtrucker.filters;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import wsb.bugtracker.models.*;

@NoArgsConstructor
@AllArgsConstructor
public class IssueFilter {

    private Long id;
    private Status status;
    private Priority priority;
    private Type type;
    private String name;
    private Project project;
    private Person assignee;
}
