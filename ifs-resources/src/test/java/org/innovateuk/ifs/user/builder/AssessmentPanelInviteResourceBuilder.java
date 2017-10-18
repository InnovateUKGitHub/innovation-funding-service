package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;

/**
 * Builder for {@link org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource}s
 */
public class AssessmentPanelInviteResourceBuilder extends BaseBuilder<AssessmentPanelInviteResource,AssessmentPanelInviteResourceBuilder> {

    private AssessmentPanelInviteResourceBuilder(List<BiConsumer<Integer, AssessmentPanelInviteResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentPanelInviteResourceBuilder newAssessmentPanelInviteResource() {
        return new AssessmentPanelInviteResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentPanelInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentPanelInviteResource>> actions) {
        return new AssessmentPanelInviteResourceBuilder(actions);
    }

    @Override
    protected AssessmentPanelInviteResource createInitial() {
        return createDefault(AssessmentPanelInviteResource.class);
    }

    public AssessmentPanelInviteResourceBuilder withStatus(InviteStatus... status) {
        return withArraySetFieldByReflection("status", status);
    }

    public AssessmentPanelInviteResourceBuilder withCompetitionId(Long... competitionId) {
        return withArraySetFieldByReflection("competitionId", competitionId);
    }

    public AssessmentPanelInviteResourceBuilder withCompetitionName(String... competitionName) {
        return withArraySetFieldByReflection("competitionName", competitionName);
    }

    public AssessmentPanelInviteResourceBuilder withInviteHash(String... hash) {
        return withArraySetFieldByReflection("hash", hash);
    }
}
