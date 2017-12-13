package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.workflow.configuration.AssessmentReviewWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.mapper.AssessmentPanelParticipantMapper;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.invite.resource.AssessmentPanelParticipantResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentPanelParticipant;
import org.innovateuk.ifs.invite.repository.AssessmentPanelParticipantRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static java.util.stream.Collectors.toList;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.CREATED;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
//import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR_PANEL;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional
public class AssessmentPanelServiceImpl implements AssessmentPanelService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentPanelParticipantRepository assessmentPanelParticipantRepository;

    @Autowired
    private AssessmentPanelParticipantMapper assessmentPanelParticipantMapper;

    @Autowired
    private AssessmentReviewWorkflowHandler workflowHandler;

    @Autowired
    private AssessmentReviewRepository assessmentReviewRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProcessRoleRepository processsRoleRepository;

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


    @Override
    public ServiceResult<Boolean> isPendingReviewNotifications(long competitionId) {

        return serviceSuccess(assessmentReviewRepository.notifiable(competitionId));
        // applications in panel
//        assessmentReviewRepository.foo(competitionId, true, CREATED);
//        return serviceSuccess(getAllAssessorsOnPanel(competitionId)
//                .map(CompetitionParticipant::getUser)
//                .allMatch(u -> assessmentReviewRepository.foo(competitionId, u))
//        );
        // this will be correct, however, right now we don't create an intermediate
//        return serviceSuccess(
//                assessmentReviewRepository.existsByTargetCompetitionIdAndActivityStateState(competitionId, State.CREATED)
//        );
    }

    private ServiceResult<Void> createAssessmentPanelApplication(AssessmentPanelParticipant assessor, Application application) {
        if (!assessmentReviewRepository.existsByParticipantUserAndTarget(assessor.getUser(), application)) {
            final ProcessRole processRole = getOrCreateAssessorProcessRoleForApplication(assessor, application);
            AssessmentReview assessmentReview =  new AssessmentReview(application, processRole);
            assessmentReview.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE, State.CREATED));
             assessmentReviewRepository.save(assessmentReview);
        }
        return serviceSuccess();
    }

    private ProcessRole getOrCreateAssessorProcessRoleForApplication(AssessmentPanelParticipant assessor, Application application) {
        final Role assessorRole = roleRepository.findOneByName(ASSESSOR_PANEL.getName());
        return processsRoleRepository.save(new ProcessRole(assessor.getUser(), application.getId(), assessorRole, null));
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }
private List<AssessmentPanelParticipant> getAllAssessorsOnPanel(long competitionId) {
        return assessmentPanelParticipantRepository.getPanelAssessorsByCompetitionAndStatusContains(competitionId, singletonList(ParticipantStatus.ACCEPTED));}

    private List<Application> getAllApplicationsOnPanel(long competitionId) {
        return applicationRepository
                .findByCompetitionIdAndInAssessmentPanelAndApplicationProcessActivityStateState(competitionId, true, State.SUBMITTED);
    }

    private ServiceResult<Void> notifyAllCreated(long competitionId) {
        assessmentReviewRepository.findByTargetCompetitionIdAndActivityStateState(competitionId, CREATED.getBackingState())
                .forEach(workflowHandler::notifyInvitation);

        // do we just catch the notify event to send the email?
        return serviceSuccess();
    }
}