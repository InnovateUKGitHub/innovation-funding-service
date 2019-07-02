package org.innovateuk.ifs.project.managestate.viewmodel;

import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.TimeZoneUtil.toUkTimeZone;

public class ProjectStateCommentViewModel {

    private String user;
    private String userRole;
    private ZonedDateTime date;
    private String comment;

    public ProjectStateCommentViewModel(PostResource post) {
        this.user = post.author.getName();
        String roleName = post.author.hasRole(Role.IFS_ADMINISTRATOR) ? "IFS Administrator" : "Finance team";
        this.userRole = format("Innovate UK (%s)", roleName);
        this.date = toUkTimeZone(post.createdOn);
        this.comment = post.body;
    }

    public String getUser() {
        return user;
    }

    public String getUserRole() {
        return userRole;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
