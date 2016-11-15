package com.worth.ifs.testdata.builders;

import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.domain.CompetitionInvite;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.assessment.builder.CompetitionInviteBuilder.newCompetitionInvite;
import static java.util.Collections.emptyList;


public class AssessorInviteDataBuilder extends BaseDataBuilder<Void, AssessorInviteDataBuilder> {

    public AssessorInviteDataBuilder withInviteToAssessCompetition(String competitionName, String emailAddress, String name, String inviteHash) {

        return with(data -> doAs(systemRegistrar(), () -> {

            CompetitionInvite invite = newCompetitionInvite().
                    withCompetition(retrieveCompetitionByName(competitionName)).
                    withEmail(emailAddress).
                    withStatus(InviteStatus.SENT).
                    withHash(inviteHash).
                    withName(name).
                    build();

            competitionInviteRepository.save(invite);
        }));
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
