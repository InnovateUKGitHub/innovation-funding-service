package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.util.List;

public interface QuestionReassignmentService {
    @NotSecured("Should be secured by calling service.")
    void reassignCollaboratorResponsesAndQuestionStatuses(Long applicationId, List<ProcessRole> collaboratorProcessRoles, ProcessRole leadApplicantProcessRole);
}
