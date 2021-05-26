package org.innovateuk.ifs.assessment.period.security;

import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod} resources.
 */
@Component
@PermissionRules
public class AssessmentPeriodPermissionRule extends BasePermissionRules {

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @PermissionRule(value = "READ", description = "Comp Admins are able to read the assessment periods for the given competitions")
    public boolean compAdminCanReadAssessmentPeriod(CompetitionResource competition, UserResource loggedInUser) {
        return SecurityRuleUtil.isCompAdmin(loggedInUser);
    }

    @PermissionRule(value = "READ", description = "Assessors part of the competition can read assessment periods for the given competitions")
    public boolean userCanReadAssessmentPeriod(CompetitionResource competition, UserResource loggedInUser) {
        return SecurityRuleUtil.hasAssessorAuthority (loggedInUser)
                && assessorHasAcceptedInvite(competition.getId(), loggedInUser);
    }

    private boolean assessorHasAcceptedInvite(Long competitionId, UserResource loggedInUser) {
        List<AssessmentParticipant> competitionParticipantList = assessmentParticipantRepository.getByAssessorId(loggedInUser.getId());

        competitionParticipantList = competitionParticipantList.stream()
                .filter(participant -> participant.getProcess().getId().equals(competitionId))
                .filter(participant -> participant.getStatus().equals(ParticipantStatus.ACCEPTED))
                .collect(toList());

        return !competitionParticipantList.isEmpty();
    }
}
