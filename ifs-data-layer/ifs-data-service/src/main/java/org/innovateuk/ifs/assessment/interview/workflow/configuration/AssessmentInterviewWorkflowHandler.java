package org.innovateuk.ifs.assessment.interview.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewEvent.NOTIFY;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW;

/**
 * Manages the process for assigning applications to assessors for an assessment interview.
 */
@Component
public class AssessmentInterviewWorkflowHandler extends BaseWorkflowEventHandler<AssessmentInterview, AssessmentInterviewState, AssessmentInterviewEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentInterviewStateMachineFactory")
    private StateMachineFactory<AssessmentInterviewState, AssessmentInterviewEvent> stateMachineFactory;

    @Autowired
    private AssessmentInterviewRepository assessmentInterviewRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected AssessmentInterview createNewProcess(Application target, ProcessRole participant) {
        return new AssessmentInterview(target, participant);
    }

    public boolean notifyInvitation(AssessmentInterview AssessmentInterview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(AssessmentInterview, NOTIFY), AssessmentInterview);
    }

    @Override
    protected ActivityType getActivityType() {
        return ASSESSMENT_INTERVIEW;
    }

    @Override
    protected ProcessRepository<AssessmentInterview> getProcessRepository() {
        return assessmentInterviewRepository;
    }

    @Override
    protected CrudRepository<Application, Long> getTargetRepository() {
        return applicationRepository;
    }

    @Override
    protected CrudRepository<ProcessRole, Long> getParticipantRepository() {
        return processRoleRepository;
    }

    @Override
    protected StateMachineFactory<AssessmentInterviewState, AssessmentInterviewEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected AssessmentInterview getOrCreateProcess(Message<AssessmentInterviewEvent> message) {
        return (AssessmentInterview) message.getHeaders().get("target");
    }

    private static MessageBuilder<AssessmentInterviewEvent> assessmentPanelApplicationInviteMessage(AssessmentInterview AssessmentInterview, AssessmentInterviewEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", AssessmentInterview);
    }
}