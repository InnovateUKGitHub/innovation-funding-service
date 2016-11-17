package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.Builder;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.invite.resource.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class CompetitionParticipantResourceBuilder extends BaseBuilder<CompetitionParticipantResource, CompetitionParticipantResourceBuilder> {

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

    public CompetitionParticipantResourceBuilder withId(Long... ids) {
        return withArray((id, competitionParticipantResource) -> setField("id", id, competitionParticipantResource), ids);
    }

    public CompetitionParticipantResourceBuilder withUser(Long... users) {
        return withArray((user, competitionParticipantResource) -> setField("userId", user, competitionParticipantResource), users);
    }

    public CompetitionParticipantResourceBuilder withCompetition(Long... competitions) {
        return withArray((competition, competitionParticipantResource) -> setField("competitionId", competition, competitionParticipantResource), competitions);
    }

    public CompetitionParticipantResourceBuilder withInvite(CompetitionInviteResource... invites) {
        return withArray((invite, competitionParticipantResource) -> setField("invite", invite, competitionParticipantResource), invites);
    }

    public CompetitionParticipantResourceBuilder withInvite(Builder<CompetitionInviteResource, ?> invite) {
        return withInvite(invite.build());
    }

    public CompetitionParticipantResourceBuilder withRejectionReason(RejectionReasonResource... rejectionReasons) {
        return withArray((reason, competitionParticipantResource) -> setField("rejectionReason", reason, competitionParticipantResource), rejectionReasons);
    }

    public CompetitionParticipantResourceBuilder withRejectionReasonComment(String... rejectionReasonComments) {
        return withArray((reasonComment, competitionParticipantResource) -> setField("rejectionReasonComment", reasonComment, competitionParticipantResource), rejectionReasonComments);
    }

    public CompetitionParticipantResourceBuilder withCompetitionParticipantRole(CompetitionParticipantRoleResource... roles) {
        return withArray((role, competitionParticipantResource) -> setField("role", role, competitionParticipantResource), roles);
    }

    public CompetitionParticipantResourceBuilder withStatus(ParticipantStatusResource... statuses) {
        return withArray((status, competitionParticipantResource) -> setField("status", status, competitionParticipantResource), statuses);
    }

    public CompetitionParticipantResourceBuilder withCompetitionName(String... competitionNames) {
        return withArray((competitionName, competitionParticipantResource) -> setField("competitionName", competitionName, competitionParticipantResource), competitionNames);
    }

    public CompetitionParticipantResourceBuilder withAssessorAcceptsDate(LocalDateTime... assessorAcceptsDates) {
        return withArray((assessorAcceptsDate, competitionParticipantResource) -> setField("assessorAcceptsDate", assessorAcceptsDate, competitionParticipantResource), assessorAcceptsDates);
    }

    public CompetitionParticipantResourceBuilder withAssessorDeadlineDate(LocalDateTime... assessorDeadlineDates) {
        return withArray((assessorDeadlineDate, competitionParticipantResource) -> setField("assessorDeadlineDate", assessorDeadlineDate, competitionParticipantResource), assessorDeadlineDates);
    }

    public CompetitionParticipantResourceBuilder withCompetitionStatus(CompetitionStatus... competitionStatuses) {
        return withArray((competitionStatus, competitionParticipantResource) -> setField("competitionStatus", competitionStatus, competitionParticipantResource), competitionStatuses);
    }

    @Override
    protected void postProcess(int index, CompetitionParticipantResource instance) {
        super.postProcess(index, instance);
    }

}