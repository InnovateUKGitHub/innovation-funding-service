package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.user.domain.ProcessRole;

import java.util.List;

public interface QuestionReassignmentService {
    void reassignCollaboratorResponsesAndQuestionStatuses(Long applicationId, List<ProcessRole> collaboratorProcessRoles, ProcessRole leadApplicantProcessRole);
}
