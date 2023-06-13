package wsb.bugtracker.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    TODO, IN_PROGRESS, DONE;

    @JsonCreator
    public static Status fromString(@JsonProperty("state") String value) {
        return Status.valueOf(value);
    }
}
