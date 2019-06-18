package org.innovateuk.ifs.threads.domain;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectState;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("PROJECT_STATE_COMMENTS")
public class ProjectStateComments extends Thread {

    @Enumerated(EnumType.STRING)
    @Column(name = "section")
    private ProjectState projectState;

    public ProjectStateComments() {
        super();
    }

    public ProjectStateComments(Long id, Long classPk, List<Post> posts, String title, ProjectState projectState, ZonedDateTime createdOn) {
        super(id, classPk, Project.class.getName(), posts, title, createdOn);
        this.projectState = projectState;
    }

    public ProjectState getProjectState() {
        return projectState;
    }
}
