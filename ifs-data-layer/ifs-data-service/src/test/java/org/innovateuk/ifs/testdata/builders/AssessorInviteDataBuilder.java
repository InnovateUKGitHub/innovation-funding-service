package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.RejectionReasonResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;

/**
 * Generates assessor invites and gives the ability to accept them
 */
public class AssessorInviteDataBuilder extends BaseDataBuilder<Void, AssessorInviteDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(AssessorInviteDataBuilder.class);

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

            final AssessmentInvite invite = newAssessmentInvite().
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

            testService.doWithinTransaction(() -> {
                AssessmentInvite savedInvite = assessmentInviteRepository.save(invite);
                assessmentParticipantRepository.save(new AssessmentParticipant(savedInvite));
            });
        }));
    }

    private User getDefaultAdminUser() {
        return userRepository.findById(16L).get();
    }

    public AssessorInviteDataBuilder acceptInvite(String hash, String assessorEmail) {
        return with(data -> {

            UserResource assessor = retrieveUserByEmail(assessorEmail);

            doAs(systemRegistrar(), () -> assessmentInviteService.openInvite(hash).getSuccess());
            doAs(assessor, () -> assessmentInviteService.acceptInvite(hash, assessor).getSuccess());
        });
    }

    public AssessorInviteDataBuilder rejectInvite(String hash, String rejectionReason, Optional<String> rejectionComment) {
        return with(data -> {
            List<RejectionReasonResource> rejectionReasons = rejectionReasonService.findAllActive().getSuccess();

            RejectionReasonResource rejectionReasonResource = rejectionReasons.stream()
                    .filter(reason -> reason.getReason().equals(rejectionReason))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("rejection reason '" + rejectionReason + "' is not valid"));

            doAs(systemRegistrar(), () -> assessmentInviteService.openInvite(hash).getSuccess());
            doAs(systemRegistrar(), () -> assessmentInviteService.rejectInvite(hash, rejectionReasonResource, rejectionComment).getSuccess());
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

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Created Assessor Invite");
    }
}
