package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isProjectFinanceUser;

@Component
@PermissionRules
public class ProjectFinanceNotePermissionRules extends BasePermissionRules{

    @Autowired
    private ProjectFinanceRepository projectFinanceRepository;

    @Autowired
    private ProjectProcessRepository projectProcessRepository;

    @PermissionRule(value = "PF_CREATE", description = "Only Project Finance Users can create Notes")
    public boolean onlyProjectFinanceUsersCanCreateNotesWithInitialPostAndIsAuthor(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user) && isProjectActive(note.contextClassPk) && noteHasInitialPostWithAuthorBeingCurrentUser(note, user);
    }

    @PermissionRule(value = "PF_CREATE", description = "Only External Finance Users can create notes")
    public boolean externalFinanceUsersCanCreateQueries(final NoteResource note, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(note.contextClassPk, user.getId()) && noteHasInitialPostWithAuthorBeingCurrentUser(note, user);
    }

    private boolean noteHasInitialPostWithAuthorBeingCurrentUser(NoteResource note, UserResource user) {
        return note.posts.size() == 1 && note.posts.get(0).author.getId().equals(user.getId());
    }

    @PermissionRule(value = "PF_ADD_POST", description = "Project Finance users can add posts to a note")
    public boolean onlyProjectFinanceUsersCanAddPosts(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user) && isProjectActive(note.contextClassPk);
    }

    @PermissionRule(value = "PF_READ", description = "Only Project Finance Users can view Notes")
    public boolean onlyProjectFinanceUsersCanViewNotes(final NoteResource note, final UserResource user) {
        return isProjectFinanceUser(user);
    }

    @PermissionRule(value = "NOTES_READ", description = "All internal users are able to see notes")
    public boolean onlyInternalUsersCanViewNotes(final NoteResource note, final UserResource user) {
        return isInternal(user);
    }

    @PermissionRule(value = "NOTES_READ", description = "Comp finance users are able to see notes")
    public boolean compFinanceUsersCanViewNotes(final NoteResource note, final UserResource user) {
        return userIsExternalFinanceOnCompetitionForProject(note.contextClassPk, user.getId());
    }

    private Optional<ProjectFinance> findProjectFinance(Long id) {
        return projectFinanceRepository.findById(id);
    }

    private boolean isProjectActive(Long projectFinance) {
        Optional<ProjectFinance> pf = findProjectFinance(projectFinance);
        if (pf.isPresent()){
            long projectId = pf.get().getProject().getId();
            return isProjectActive(projectId);
        }
        return false;
    }

}