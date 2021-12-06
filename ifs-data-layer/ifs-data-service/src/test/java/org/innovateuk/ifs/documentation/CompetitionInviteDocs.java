package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.builder.*;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;

import java.math.BigDecimal;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationListResourceBuilder.newStagedApplicationListResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;

public class CompetitionInviteDocs {

    public static final CompetitionInviteResourceBuilder competitionInviteResourceBuilder = newCompetitionInviteResource()
            .withIds(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withCompetitionFundingType(FundingType.GRANT)
            .withHash("0519d73a-f062-4784-ae86-7a933a7de4c3")
            .withEmail("paul.plum@gmail.com")
            .withStatus(CREATED)
            .withAssessorPay(BigDecimal.valueOf(100L))
            .withInnovationArea(newInnovationAreaResource()
                    .withId(10L)
                    .withName("Emerging Tech and Industries")
                    .withSector(3L)
                    .build());

    public static final CompetitionRejectionResource competitionInviteResource =
            new CompetitionRejectionResource(newRejectionReasonResource()
                    .withId(1L)
                    .build(),
                    "own company");

    public static final AssessorInviteSendResourceBuilder assessorInviteSendResourceBuilder = newAssessorInviteSendResource()
            .withSubject("Subject to send")
            .withContent("E-mail body to send");

    public static final AssessorInvitesToSendResourceBuilder assessorInvitesToSendResourceBuilder = newAssessorInvitesToSendResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withContent("E-mail body content which is editable")
            .withRecipients(singletonList("Paul Plum"));

    public static final StagedApplicationResourceBuilder stagedApplicationResourceBuilder = newStagedApplicationResource()
            .withCompetitionId(1L);

    public static final ExistingUserStagedInviteResourceBuilder existingUserStagedInviteResourceBuilder = newExistingUserStagedInviteResource()
            .withCompetitionId(1L);

    public static final NewUserStagedInviteResourceBuilder newUserStagedInviteResourceBuilder = newNewUserStagedInviteResource()
            .withEmail("paul.plum@gmail.com")
            .withCompetitionId(1L)
            .withName("Paul Plum")
            .withInnovationAreaId(8L);

    public static final NewUserStagedInviteListResourceBuilder newUserStagedInviteListResourceBuilder = newNewUserStagedInviteListResource()
            .withInvites(newUserStagedInviteResourceBuilder.build(2));

    public static final ExistingUserStagedInviteListResourceBuilder existingUserStagedInviteListResourceBuilder = newExistingUserStagedInviteListResource()
            .withInvites(existingUserStagedInviteResourceBuilder.build(2));

    public static final StagedApplicationListResourceBuilder stagedApplicationListResourceBuilder = newStagedApplicationListResource()
            .withInvites(stagedApplicationResourceBuilder.build(2));
}
