package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;


public class CompetitionParticipantResourceBuilder  extends BaseBuilder<CompetitionParticipantResource, CompetitionParticipantResourceBuilder> {

    private CompetitionParticipantResourceBuilder(List<BiConsumer<Integer, CompetitionParticipantResource>> multiActions) {
        super(multiActions);
    }

    public static CompetitionParticipantResourceBuilder newCompetitionParticipantResource() {
        return new CompetitionParticipantResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected CompetitionParticipantResource createInitial() {
        return new CompetitionParticipantResource();
    }

    @Override
    protected CompetitionParticipantResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, CompetitionParticipantResource>> actions) {
        return new CompetitionParticipantResourceBuilder(actions);
    }

    public CompetitionParticipantResourceBuilder withId(Long id) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setId(id));
    }

    public CompetitionParticipantResourceBuilder withUser(Long id) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setUserId(id));
    }

    public CompetitionParticipantResourceBuilder withCompetition(Long id) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setCompetitionId(id));
    }

    public CompetitionParticipantResourceBuilder withInvite(Long id) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setInviteId(id));
    }

    public CompetitionParticipantResourceBuilder withRejectionReason(RejectionReasonResource rejectionReason) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setRejectionReason(rejectionReason));
    }

    public CompetitionParticipantResourceBuilder withRejectionReasonComment(String rejectionReasonComment) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setRejectionReasonComment(rejectionReasonComment));
    }

    public CompetitionParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource role) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setRole(role));
    }

    public CompetitionParticipantResourceBuilder withStatus(ParticipantStatusResource status) {
        return with((CompetitionParticipantResource) -> CompetitionParticipantResource.setStatus(status));
    }

    public CompetitionParticipantResourceBuilder withIds(Long... ids) {
        return withArray((id, competitionParticipantResource) -> setField("id", id, competitionParticipantResource), ids);
    }

    public CompetitionParticipantResourceBuilder withUsers(Long... users) {
        return withArray((user, competitionParticipantResource) -> setField("userId", user, competitionParticipantResource), users);
    }

    public CompetitionParticipantResourceBuilder withCompetitions(Long... competitions) {
        return withArray((competition, competitionParticipantResource) -> setField("competitionId", competition, competitionParticipantResource), competitions);
    }

    public CompetitionParticipantResourceBuilder withInvites(Long... invites) {
        return withArray((invite, competitionParticipantResource) -> setField("inviteId", invite, competitionParticipantResource), invites);
    }

    public CompetitionParticipantResourceBuilder withRejectionReasons(RejectionReasonResource... rejectionReasons) {
        return withArray((reason, competitionParticipantResource) -> setField("rejectionReason", reason, competitionParticipantResource), rejectionReasons);
    }

    public CompetitionParticipantResourceBuilder withRejectionReasonComments(String... rejectionReasonComments) {
        return withArray((reasonComment, competitionParticipantResource) -> setField("rejectionReasonComment", reasonComment, competitionParticipantResource), rejectionReasonComments);
    }

    public CompetitionParticipantResourceBuilder withCompetitionParticipantRoles(CompetitionParticipantRoleResource... roles) {
        return withArray((role, competitionParticipantResource) -> setField("role", role, competitionParticipantResource), roles);
    }

    public CompetitionParticipantResourceBuilder withStatuses(final ParticipantStatusResource... statuses) {
        return withArray((status, inviteResource) -> setField("status", status, inviteResource), statuses);
    }

    @Override
    protected void postProcess(int index, CompetitionParticipantResource instance) {
        super.postProcess(index, instance);
    }
}