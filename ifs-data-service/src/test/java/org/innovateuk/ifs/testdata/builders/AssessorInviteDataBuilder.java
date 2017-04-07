package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;

/**
 * Generates assessor invites and gives the ability to accept them
 */
public class AssessorInviteDataBuilder extends BaseDataBuilder<Void, AssessorInviteDataBuilder> {

    public AssessorInviteDataBuilder withInviteToAssessCompetition(String competitionName,
                                                                   String emailAddress,
                                                                   String name,
                                                                   String inviteHash,
                                                                   InviteStatus inviteStatus,
                                                                   Optional<User> existingUser,
                                                                   String innovationAreaName,
                                                                   Optional<User> sentBy,
                                                                   Optional<ZonedDateTime> sentOn
    ) {

        return with(data -> doAs(systemRegistrar(), () -> {

            final Competition competition = retrieveCompetitionByName(competitionName);
            final InnovationArea innovationArea = retrieveInnovationAreaByName(innovationAreaName);

            final CompetitionInvite invite = newCompetitionInvite().
                    withCompetition(competition).
                    withEmail(emailAddress).
                    withStatus(inviteStatus).
                    withHash(inviteHash).
                    withName(name).
                    withUser(existingUser.orElse(null)).
                    withInnovationArea(innovationArea).
                    withSentBy(sentBy.orElse(getDefaultAdminUser())).
                    withSentOn(sentOn.orElse(ZonedDateTime.now())).
                    build();

            CompetitionInvite savedInvite = competitionInviteRepository.save(invite);
            competitionParticipantRepository.save(new CompetitionParticipant(savedInvite));
        }));
    }

    private User getDefaultAdminUser() {
        return userRepository.findOne(16L);
    }

    public AssessorInviteDataBuilder acceptInvite(String hash, String assessorEmail) {
        return with(data -> {

            UserResource assessor = retrieveUserByEmail(assessorEmail);

            doAs(systemRegistrar(), () -> competitionInviteService.openInvite(hash).getSuccessObjectOrThrowException());
            doAs(assessor, () -> competitionInviteService.acceptInvite(hash, assessor).getSuccessObjectOrThrowException());
        });
    }

    public AssessorInviteDataBuilder rejectInvite(String hash, String rejectionReason, Optional<String> rejectionComment) {
        return with(data -> {
            List<RejectionReasonResource> rejectionReasons = rejectionReasonService.findAllActive().getSuccessObjectOrThrowException();

            RejectionReasonResource rejectionReasonResource = rejectionReasons.stream()
                    .filter(reason -> reason.getReason().equals(rejectionReason))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("rejection reason '" + rejectionReason + "' is not valid"));

            doAs(systemRegistrar(), () -> competitionInviteService.openInvite(hash).getSuccessObjectOrThrowException());
            doAs(systemRegistrar(), () -> competitionInviteService.rejectInvite(hash, rejectionReasonResource, rejectionComment).getSuccessObjectOrThrowException());
        });
    }

    public static AssessorInviteDataBuilder newAssessorInviteData(ServiceLocator serviceLocator) {
        return new AssessorInviteDataBuilder(emptyList(), serviceLocator);
    }

    private AssessorInviteDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                      ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);
    }

    @Override
    protected AssessorInviteDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new AssessorInviteDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    private InnovationArea retrieveInnovationAreaByName(String name) {
        return !isBlank(name) ? innovationAreaRepository.findByName(name) : null;
    }
}
