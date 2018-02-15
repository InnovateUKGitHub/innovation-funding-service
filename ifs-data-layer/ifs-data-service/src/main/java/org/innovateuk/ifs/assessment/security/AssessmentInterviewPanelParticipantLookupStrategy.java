package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.invite.domain.competition.InterviewParticipant;
import org.innovateuk.ifs.invite.mapper.AssessmentInterviewPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.AssessmentInterviewPanelParticipantRepository;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Lookup strategy for {@link InterviewParticipant}, used for permissioning.
 */
@Component
@PermissionEntityLookupStrategies
public class AssessmentInterviewPanelParticipantLookupStrategy {

    @Autowired
    private AssessmentInterviewPanelParticipantRepository assessmentInterviewPanelParticipantRepository;

    @Autowired
    private AssessmentInterviewPanelParticipantMapper assessmentInterviewPanelParticipantMapper;

    @PermissionEntityLookupStrategy
    public InterviewParticipantResource getAssessmentInterviewPanelParticipantResource(String inviteHash) {
        return assessmentInterviewPanelParticipantMapper.mapToResource(assessmentInterviewPanelParticipantRepository.getByInviteHash(inviteHash));
    }
}
