package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.threads.resource.NoteResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.security.SecurityRuleUtil.isProjectFinanceUser;

@Component
@PermissionRules
public class NotePermissionRules {

    @PermissionRule(value = "CREATE", description = "Only Internal Users can create Notes")
    public boolean onlyInternalUsersCanCreateNotes(final NoteResource query, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "ADD_POST", description = "Internal users can add posts to a note")
    public boolean onlyInternalUsersCanAddPosts(final NoteResource query, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "DELETE", description = "Only Internal Users can delete a Note")
    public boolean onlyInternalUsersCanDeleteNotes(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "VIEW", description = "Only Internal of Project Finance Users can view Notes")
    public boolean onlyInternalUsersCanViewNotes(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user);
    }
}