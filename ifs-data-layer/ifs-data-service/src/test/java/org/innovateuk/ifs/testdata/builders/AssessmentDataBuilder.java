package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
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

            Application application = applicationRepository.findByName(applicationName).get(0);

            AssessmentResource assessmentResource = doAs(compAdmin(), () -> assessmentService.createAssessment(
                    new AssessmentCreateResource(application.getId(), assessor.getId())).getSuccess()
            );

            testService.doWithinTransaction(() -> {

                Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
                doAs(compAdmin(), () -> assessmentWorkflowHandler.notify(assessment));
            });

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
                    Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
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

            Application application = applicationRepository.findByName(applicationName).get(0);
            UserResource assessor = retrieveUserByEmail(assessorEmail);
            Optional<Assessment> assessment = assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(assessor.getId(), application.getId());

            if (!assessment.isPresent()) {
                return;
            }

            // We have to forcefully set the SUBMITTED state for the assessment, as the
            // relevant competition is not necessarily IN_ASSESSMENT.
            // This means that the state transition to SUBMITTED through the workflow
            // handler will fail due to the `CompetitionInAssessmentGuard`.
            assessment.ifPresent(a -> a.setProcessState(AssessmentState.SUBMITTED));
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