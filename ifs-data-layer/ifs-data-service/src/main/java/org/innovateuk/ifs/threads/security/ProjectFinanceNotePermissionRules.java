package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@Component
@PermissionRules
public class ProjectFinanceNotePermissionRules {

    @PermissionRule(value = "PF_CREATE", description = "Only Project Finance Users can create Notes")
    public boolean onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user) && noteHasInitialPostWithAuthorBeingCurrentUser(note, user);
    }

    private boolean noteHasInitialPostWithAuthorBeingCurrentUser(NoteResource note, UserResource user) {
        return note.posts.size() == 1 && note.posts.get(0).author.getId().equals(user.getId());
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project Finance users can add posts to a note")
    public boolean onlyProjectFinanceUsersCanAddPosts(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "PF_READ", description = "Only Project Finance Users can view Notes")
    public boolean onlyProjectFinanceUsersCanViewNotes(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user);
    }
}