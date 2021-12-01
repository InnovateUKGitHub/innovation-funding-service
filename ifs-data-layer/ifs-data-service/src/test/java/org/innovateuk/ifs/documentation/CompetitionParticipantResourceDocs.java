package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;

public class CompetitionParticipantResourceDocs {

    public static final CompetitionParticipantResourceBuilder competitionParticipantResourceBuilder = newCompetitionParticipantResource()
            .withId(1L)
            .withCompetition(2L)
            .withUser(3L)
            .withInvite(newCompetitionInviteResource().with(id(4L)).build())
            .withCompetitionName("Juggling Craziness")
            .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
            .withStatus(ParticipantStatusResource.ACCEPTED)
            .withSubmittedAssessments(1L)
            .withTotalAssessments(4L)
            .withRejectionReason(new RejectionReasonResource("conflict", true, 1))
            .withRejectionReasonComment("Reason comment")
            .withAssessorAcceptsDate(ZonedDateTime.now().plusDays(35))
            .withAssessorDeadlineDate(ZonedDateTime.now().plusDays(40));
}
