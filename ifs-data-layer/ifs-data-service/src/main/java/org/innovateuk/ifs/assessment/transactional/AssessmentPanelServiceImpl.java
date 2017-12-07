package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.workflow.configuration.AssessmentReviewWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.CREATED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR_PANEL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional
public class AssessmentPanelServiceImpl implements AssessmentPanelService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentReviewWorkflowHandler workflowHandler;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ServiceResult<Void> assignApplicationToPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentPanel(true));
    }

    @Override
    public ServiceResult<Void> unassignApplicationFromPanel(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccessReturnVoid(application -> application.setInAssessmentPanel(false));
    }

    @Override
    public ServiceResult<Void> createAndNotifyAll(long competitionId) {
        getAllAssessorsOnPanel(competitionId)
                .forEach(assessor -> getAllApplicationsOnPanel(competitionId)
                        .forEach(application -> createAssessmentPanelApplication(assessor, application)));

        // deliberately keeping this separate from creation in anticipation of splitting out notification
        return notifyAllCreated(competitionId);
    }

    private ServiceResult<Void> createAssessmentPanelApplication(AssessmentPanelParticipant assessor, Application application) {
        if (!assessmentReviewRepository.existsByParticipantUserAndTarget(assessor.getUser(), application)) {
            final ProcessRole processRole = getOrCreateAssessorProcessRoleForApplication(assessor, application);
             assessmentReviewRepository.save(new AssessmentReview(application, processRole));
        }
        return serviceSuccess();
    }

    private ProcessRole getOrCreateAssessorProcessRoleForApplication(AssessmentPanelParticipant assessor, Application application) {
        final Role assessorRole = roleRepository.findOneByName(ASSESSOR_PANEL.getName());
        return new ProcessRole(assessor.getUser(), application.getId(), assessorRole, null);
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private Stream<AssessmentPanelParticipant> getAllAssessorsOnPanel(long competitionId) {
        return assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED)).stream();
    }

    private Stream<Application> getAllApplicationsOnPanel(long competitionId) {
        return applicationRepository
                .findByCompetitionIdAndInAssessmentPanelAndApplicationProcessActivityStateState(competitionId, true, State.SUBMITTED);
    }

    private ServiceResult<Void> notifyAllCreated(long competitionId) {
        assessmentReviewRepository.findByTargetIdAndActivityStateState(competitionId, CREATED.getBackingState())
                .forEach(workflowHandler::notifyInvitation);

        // do we just catch the notify event to send the email?
        return serviceSuccess();
    }
}