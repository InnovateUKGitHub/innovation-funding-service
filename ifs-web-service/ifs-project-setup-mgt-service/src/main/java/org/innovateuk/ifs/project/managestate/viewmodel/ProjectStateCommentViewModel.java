package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;

public class ProjectStateCommentViewModel {

    private String user;
    private String userRole;
    private ZonedDateTime date;
    private String comment;

    public ProjectStateCommentViewModel(PostResource post) {
        this.user = post.author.getName();
        this.userRole = post.author.hasRole(Role.IFS_ADMINISTRATOR) ? "IFS Administrator" : "Innovate UK (";
        this.date = date;
        this.comment = comment;
    }
}
