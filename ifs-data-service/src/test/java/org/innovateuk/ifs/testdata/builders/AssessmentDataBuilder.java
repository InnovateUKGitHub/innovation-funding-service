package org.innovateuk.ifs.testdata.builders;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;

import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * Generates Assessments for Applications so that Assessors may start assessing them
 */
public class AssessmentDataBuilder extends BaseDataBuilder<Void, AssessmentDataBuilder> {

    public AssessmentDataBuilder withAssessmentData(String assessorEmail,
                                                    String applicationName,
                                                    AssessmentRejectOutcomeValue rejectReason,
                                                    String rejectComment,
                                                    AssessmentStates state) {
        return with(data -> {

            Application application = applicationRepository.findByName(applicationName).get(0);
            UserResource assessor = retrieveUserByEmail(assessorEmail);

            AssessmentResource assessmentResource = doAs(compAdmin(), () -> assessmentService.createAssessment(
                    new AssessmentCreateResource(application.getId(), assessor.getId())).getSuccessObjectOrThrowException()
            );

            Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
            doAs(compAdmin(), () -> assessmentWorkflowHandler.notify(assessment));

            switch (state) {
                case ACCEPTED:
                    doAs(assessor, () -> assessmentService.acceptInvitation(assessment.getId())
                            .getSuccessObjectOrThrowException());
                    break;
                case REJECTED:
                    doAs(assessor, () -> assessmentService.rejectInvitation(assessment.getId(), new
                            AssessmentRejectOutcomeResource(rejectReason, rejectComment))
                            .getSuccessObjectOrThrowException());
                    break;
                case WITHDRAWN:
                    doAs(compAdmin(), () -> assessmentService.withdrawAssessment(assessment.getId())
                            .getSuccessObjectOrThrowException());
                    break;
            }

            // TODO INFUND-8137 without feedback these states must be set directly
            // Adding of the feedback will result in the assessment state being updated and allow for the assessment
            // to eventually be submitted.
            if (EnumSet.of(OPEN, READY_TO_SUBMIT, SUBMITTED).contains(state)) {
                ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(ActivityType
                        .APPLICATION_ASSESSMENT, state.getBackingState());
                assessment.setActivityState(activityState);
            }
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
}
