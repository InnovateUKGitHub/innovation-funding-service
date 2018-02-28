package org.innovateuk.ifs.assessment.interview.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelMessageOutcome;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelResponseOutcome;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
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

import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_INTERVIEW_PANEL;

/**
 * Manages the process for assigning applications to assessors for an assessment interview.
 */
@Component
public class AssessmentInterviewPanelWorkflowHandler extends BaseWorkflowEventHandler<AssessmentInterviewPanel, AssessmentInterviewPanelState, AssessmentInterviewPanelEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentInterviewPanelStateMachineFactory")
    private StateMachineFactory<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> stateMachineFactory;

    @Autowired
    private AssessmentInterviewPanelRepository assessmentInterviewPanelRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected AssessmentInterviewPanel createNewProcess(Application target, ProcessRole participant) {
        return new AssessmentInterviewPanel(target, participant, null);
    }

    public boolean notifyInterviewPanel(AssessmentInterviewPanel assessmentInterviewPanel, AssessmentInterviewPanelMessageOutcome messageOutcome) {
        return fireEvent(notifyMessage(assessmentInterviewPanel, messageOutcome), assessmentInterviewPanel);
    }

    public boolean respondToInterviewPanel(AssessmentInterviewPanel assessmentInterviewPanel, AssessmentInterviewPanelResponseOutcome responseOutcome) {
        return fireEvent(responseMessage(assessmentInterviewPanel, responseOutcome), assessmentInterviewPanel);
    }

    @Override
    protected ActivityType getActivityType() {
        return ASSESSMENT_INTERVIEW_PANEL;
    }

    @Override
    protected ProcessRepository<AssessmentInterviewPanel> getProcessRepository() {
        return assessmentInterviewPanelRepository;
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
    protected StateMachineFactory<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected AssessmentInterviewPanel getOrCreateProcess(Message<AssessmentInterviewPanelEvent> message) {
        return (AssessmentInterviewPanel) message.getHeaders().get("target");
    }

    private MessageBuilder<AssessmentInterviewPanelEvent> notifyMessage(AssessmentInterviewPanel assessmentInterviewPanel, AssessmentInterviewPanelMessageOutcome messageOutcome) {
        return assessmentInterviewPanelMessage(assessmentInterviewPanel, AssessmentInterviewPanelEvent.NOTIFY)
                .setHeader("message", messageOutcome);
    }

    private MessageBuilder<AssessmentInterviewPanelEvent> responseMessage(AssessmentInterviewPanel assessmentInterviewPanel, AssessmentInterviewPanelResponseOutcome responseOutcome) {
        return assessmentInterviewPanelMessage(assessmentInterviewPanel, AssessmentInterviewPanelEvent.RESPOND)
                .setHeader("response", responseOutcome);
    }

    private static MessageBuilder<AssessmentInterviewPanelEvent> assessmentInterviewPanelMessage(AssessmentInterviewPanel assessmentInterviewPanel, AssessmentInterviewPanelEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", assessmentInterviewPanel);
    }
}