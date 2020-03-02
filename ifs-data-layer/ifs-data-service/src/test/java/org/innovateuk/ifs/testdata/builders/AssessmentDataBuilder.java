package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.SUBMITTED;

/**
 * Generates Assessments for Applications so that Assessors may start assessing them
 */
public class AssessmentDataBuilder extends BaseDataBuilder<Void, AssessmentDataBuilder> {

    private static final Logger LOG = LoggerFactory.getLogger(AssessmentDataBuilder.class);

    public AssessmentDataBuilder withAssessmentData(String assessorEmail,
                                                    String applicationName,
                                                    AssessmentRejectOutcomeValue rejectReason,
                                                    String rejectComment,
                                                    AssessmentState state,
                                                    String feedback,
                                                    String recommendComment) {
        return with(data -> {

            UserResource assessor = retrieveUserByEmail(assessorEmail);

            if (!assessor.hasRole(Role.ASSESSOR)) {
                testService.doWithinTransaction(() -> {

                    User user = userRepository.findByEmail(assessor.getEmail()).get();

                    if (!user.getRoles().contains(Role.ASSESSOR)) {
                        user.getRoles().add(Role.ASSESSOR);
                    }

                    userRepository.save(user);
                });
            }
            Application application = applicationRepository.findByName(applicationName).get(0);

            AssessmentResource assessmentResource = doAs(compAdmin(), () -> assessmentService.createAssessment(
                    new AssessmentCreateResource(application.getId(), assessor.getId())).getSuccess()
            );

            doAs(compAdmin(), () ->
                testService.doWithinTransaction(() -> {
                    Assessment assessment = assessmentRepository.findById(assessmentResource.getId()).get();
                    assessmentWorkflowHandler.notify(assessment);
                })
            );

            switch (state) {
                case ACCEPTED:
                case READY_TO_SUBMIT:
                case SUBMITTED:
                    doAs(assessor, () -> assessmentService.acceptInvitation(assessmentResource.getId())
                            .getSuccess());
                    break;
                case REJECTED:
                    doAs(assessor, () -> assessmentService.rejectInvitation(assessmentResource.getId(), new
                            AssessmentRejectOutcomeResource(rejectReason, rejectComment))
                            .getSuccess());
                    break;
                case WITHDRAWN:
                    doAs(compAdmin(), () -> assessmentService.withdrawAssessment(assessmentResource.getId())
                            .getSuccess());
                    break;
            }

            if (EnumSet.of(OPEN).contains(state)) {
                testService.doWithinTransaction(() -> {
                    Assessment assessment = assessmentRepository.findById(assessmentResource.getId()).get();
                    assessment.setProcessState(state);
                });
            }

            doAs(assessor, () -> {
                if (feedback == null && recommendComment == null) {
                    return;
                }

                AssessmentFundingDecisionOutcomeResource fundingDecision = new AssessmentFundingDecisionOutcomeResource(
                        true,
                        feedback,
                        recommendComment
                );

                assessmentService.recommend(assessmentResource.getId(), fundingDecision).getSuccess();
            });
        });
    }

    public AssessmentDataBuilder withSubmission(String applicationName,
                                                String assessorEmail,
                                                AssessmentState state) {
        return with(data -> {
            if (state != SUBMITTED) {
                return;
            }

            // We have to forcefully set the SUBMITTED state for the assessment, as the
            // relevant competition is not necessarily IN_ASSESSMENT.
            // This means that the state transition to SUBMITTED through the workflow
            // handler will fail due to the `CompetitionInAssessmentGuard`.

            Application application = applicationRepository.findByName(applicationName).get(0);
            UserResource assessor = retrieveUserByEmail(assessorEmail);
            doAs(assessor, () -> {
                testService.doWithinTransaction(() -> {

                    Optional<Assessment> assessment = assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessor.getId(), application.getId());

                    if (!assessment.isPresent()) {
                        return;
                    }

                    assessment.ifPresent(a -> {
                        a.setProcessEvent(AssessmentEvent.SUBMIT.getType());
                        a.setProcessState(AssessmentState.SUBMITTED);
                    });
                });
            });
        });
    }

    public AssessmentDataBuilder withPanelAssignment(String applicationName, String assessorEmail, CompetitionResource competition) {
        return with(data -> {
            Application application = applicationRepository.findByName(applicationName).get(0);
            UserResource assessor = retrieveUserByEmail(assessorEmail);

            doAs(compAdmin(), () -> {
                ExistingUserStagedInviteResource invite = new ExistingUserStagedInviteResource(assessor.getId(), competition.getId());
                reviewInviteService.inviteUsers(singletonList(invite)).getSuccess();
                reviewInviteService.sendAllInvites(competition.getId(), new AssessorInviteSendResource("", "")).getSuccess();
            });

            ReviewInvite invite = reviewInviteRepository.getByEmailAndCompetitionId(assessor.getEmail(), competition.getId());

            doAs(systemRegistrar(), () ->
                reviewInviteService.openInvite(invite.getHash()).getSuccess());

            doAs(assessor, () ->
                reviewInviteService.acceptInvite(invite.getHash()).getSuccess());
        });
    }

    public AssessmentDataBuilder withInterviewAssignment(String applicationName, String assessorEmail, CompetitionResource competition) {
        return with(data -> {
            Application application = applicationRepository.findByName(applicationName).get(0);
            UserResource assessor = retrieveUserByEmail(assessorEmail);

            doAs(compAdmin(), () -> {
                ExistingUserStagedInviteResource invite = new ExistingUserStagedInviteResource(assessor.getId(), competition.getId());
                interviewInviteService.inviteUsers(singletonList(invite)).getSuccess();
                interviewInviteService.sendAllInvites(competition.getId(), new AssessorInviteSendResource("", "")).getSuccess();
            });

            InterviewInvite invite = interviewInviteRepository.getByEmailAndCompetitionId(assessor.getEmail(), competition.getId());

            doAs(systemRegistrar(), () ->
                    interviewInviteService.openInvite(invite.getHash()).getSuccess());

            doAs(assessor, () ->
                    interviewInviteService.acceptInvite(invite.getHash()).getSuccess());
        });
    }

    public static AssessmentDataBuilder newAssessmentData(ServiceLocator serviceLocator) {

        return new AssessmentDataBuilder(emptyList(), serviceLocator);
    }

    private AssessmentDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                  ServiceLocator serviceLocator) {

        super(multiActions, serviceLocator);
    }

    @Override
    protected AssessmentDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new AssessmentDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }

    @Override
    protected void postProcess(int index, Void instance) {
        super.postProcess(index, instance);
        LOG.info("Created Assessment", instance);
    }

}