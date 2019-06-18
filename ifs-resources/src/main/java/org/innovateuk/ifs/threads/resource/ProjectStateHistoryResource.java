package org.innovateuk.ifs.threads.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;

public class ProjectStateHistoryResource {
    public final Long id;
    public final Long contextClassPk;
    public final List<PostResource> posts;
    public final ProjectState state;
    public final String title;
    public final ZonedDateTime createdOn;
    public final UserResource closedBy;
    public final ZonedDateTime closedDate;

    @JsonCreator
    public ProjectStateHistoryResource(@JsonProperty("id") Long id, @JsonProperty("contextClassPk") Long contextClassPk,
                                       @JsonProperty("posts") List<PostResource> posts, @JsonProperty("state") ProjectState state,
                                       @JsonProperty("title") String title,
                                       @JsonProperty("createdOn") ZonedDateTime createdOn,
                                       @JsonProperty("closedBy") UserResource closedBy,
                                       @JsonProperty("closedDate") ZonedDateTime closedDate) {
        this.id = id;
        this.contextClassPk = contextClassPk;
        this.posts = posts;
        this.state = state;
        this.title = title;
        this.createdOn = createdOn;
        this.closedBy = closedBy;
        this.closedDate = closedDate;
    }
}
