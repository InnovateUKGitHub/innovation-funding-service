package com.worth.ifs.assessment.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.invite.resource.CompetitionInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.invite.resource.CompetitionInviteResource}
 */
public class CompetitionInviteResourceBuilder extends BaseBuilder<CompetitionInviteResource, CompetitionInviteResourceBuilder> {

    private CompetitionInviteResourceBuilder(List<BiConsumer<Integer, CompetitionInviteResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionInviteResourceBuilder newCompetitionInviteResource() {
        return new CompetitionInviteResourceBuilder(emptyList());
    }

    public CompetitionInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public CompetitionInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, inviteResource) -> inviteResource.setCompetitionName(competitionName), competitionNames);
    }

    public CompetitionInviteResourceBuilder withCompetitionName(Builder<String, ?> competitionName) {
        return withCompetitionName(competitionName.build());
    }

    public CompetitionInviteResourceBuilder withEmail(String... emails) {
        return withArray((email, inviteResource) -> inviteResource.setEmail(email), emails);
    }

    @Override
    protected CompetitionInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionInviteResource>> actions) {
        return new CompetitionInviteResourceBuilder(actions);
    }

    @Override
    protected CompetitionInviteResource createInitial() {
        return new CompetitionInviteResource();
    }
}
