package org.innovateuk.ifs.interview.workflow.configuration;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.repository.InterviewRepository;
import org.innovateuk.ifs.interview.resource.InterviewEvent;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowEventHandler;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.interview.resource.InterviewEvent.NOTIFY;

/**
 * Manages the process for assigning applications to assessors for an assessment interview.
 */
@Component
public class InterviewWorkflowHandler extends BaseWorkflowEventHandler<Interview, InterviewState, InterviewEvent, Application, ProcessRole> {

    @Autowired
    @Qualifier("assessmentInterviewStateMachineFactory")
    private StateMachineFactory<InterviewState, InterviewEvent> stateMachineFactory;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Override
    protected Interview createNewProcess(Application target, ProcessRole participant) {
        return new Interview(target, participant);
    }

    public boolean notifyInvitation(Interview interview) {
        return fireEvent(assessmentPanelApplicationInviteMessage(interview, NOTIFY), interview);
    }

    @Override
    protected ProcessRepository<Interview> getProcessRepository() {
        return interviewRepository;
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
    protected StateMachineFactory<InterviewState, InterviewEvent> getStateMachineFactory() {
        return stateMachineFactory;
    }

    @Override
    protected Interview getOrCreateProcess(Message<InterviewEvent> message) {
        return (Interview) message.getHeaders().get("target");
    }

    private static MessageBuilder<InterviewEvent> assessmentPanelApplicationInviteMessage(Interview interview, InterviewEvent event) {
        return MessageBuilder
                .withPayload(event)
                .setHeader("target", interview);
    }
}