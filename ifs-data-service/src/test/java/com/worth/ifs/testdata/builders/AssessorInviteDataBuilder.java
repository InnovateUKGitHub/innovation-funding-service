package com.worth.ifs.testdata.builders;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static java.util.Collections.emptyList;

/**
 * Generates assessor invites and gives the ability to accept them
 */
public class AssessorInviteDataBuilder extends BaseDataBuilder<Void, AssessorInviteDataBuilder> {

    public AssessorInviteDataBuilder withInviteToAssessCompetition(String competitionName, String emailAddress, String name, String inviteHash, Optional<User> existingUser) {

        return with(data -> doAs(systemRegistrar(), () -> {

            final Competition competition = retrieveCompetitionByName(competitionName);

            final CompetitionInvite invite = newCompetitionInvite().
                    withCompetition(competition).
                    withEmail(emailAddress).
                    withStatus(InviteStatus.SENT).
                    withHash(inviteHash).
                    withName(name).
                    withUser(existingUser.orElse(null)).
                    build();

            CompetitionInvite savedInvite = competitionInviteRepository.save(invite);
            competitionParticipantRepository.save(new CompetitionParticipant(savedInvite));
        }));
    }

    public AssessorInviteDataBuilder acceptInvite(String hash, String assessorEmail) {
        return with(data -> {

            UserResource assessor = retrieveUserByEmail(assessorEmail);

            doAs(systemRegistrar(), () -> competitionInviteService.openInvite(hash).getSuccessObjectOrThrowException());
            doAs(assessor, () -> competitionInviteService.acceptInvite(hash, assessor).getSuccessObjectOrThrowException());
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


}
