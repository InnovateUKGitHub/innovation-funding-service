package org.innovateuk.ifs.assessment.interview.transactional;


import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


/*
 * Service for managing {@link AssessmentInterviewPanelInvite}s.
 */
@Service
@Transactional()
public class InterviewPanelInviteServiceImpl implements InterviewPanelInviteService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private AssessmentInterviewPanelRepository assessmentInterviewPanelRepository;


    @Autowired
    private OrganisationRepository organisationRepository;

    @Override
    public ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable) {

            final Page<Application> pagedResult =
                    applicationRepository.findSubmittedApplicationsNotOnInterviewPanel(competitionId, pageable);

            return serviceSuccess(new AvailableApplicationPageResource(
                    pagedResult.getTotalElements(),
                    pagedResult.getTotalPages(),
                    simpleMap(pagedResult.getContent(), this::mapToAvailableApplicationResource),
                    pagedResult.getNumber(),
                    pagedResult.getSize()
            ));
        }

    @Override
    @Transactional
    public ServiceResult<InterviewPanelStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable) {
        final Page<AssessmentInterviewPanel> pagedResult =
                assessmentInterviewPanelRepository.findByTargetCompetitionIdAndActivityStateState(
                        competitionId, AssessmentInterviewPanelState.CREATED.getBackingState(), pageable);


        return serviceSuccess(new InterviewPanelStagedApplicationPageResource(
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                simpleMap(pagedResult.getContent(), this::mapToPanelCreatedInviteResource),
                pagedResult.getNumber(),
                pagedResult.getSize()
        ));

    }

    @Override
    public ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId) {
        return serviceSuccess(
                simpleMap(
                        applicationRepository.findSubmittedApplicationsNotOnInterviewPanel(competitionId),
                        Application::getId
                )
        );
    }

    @Override
    public ServiceResult<Void> assignApplications(List<ExistingUserStagedInviteResource> stagedInvites) {
        stagedInvites.forEach(invite -> getApplication(invite.getUserId()).andOnSuccess(this::assignApplicationToCompetition));
        return serviceSuccess();
    }

    private ServiceResult<Application> getApplication(long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId));
    }

    private AvailableApplicationResource mapToAvailableApplicationResource(Application application) {
        final Organisation leadOrganisation = organisationRepository.findOne(application.getLeadOrganisationId());

        return getOrganisation(application.getLeadOrganisationId())
                .andOnSuccessReturn(
                        organisation ->
                                new AvailableApplicationResource(application.getId(), application.getName(), leadOrganisation.getName()
                        )
                ).getSuccess();
    }

    private InterviewPanelStagedApplicationResource mapToPanelCreatedInviteResource(AssessmentInterviewPanel panelInvite) {
        final Application application = panelInvite.getTarget();

        return getOrganisation(application.getLeadOrganisationId())
                .andOnSuccessReturn(leadOrganisation ->
                        new InterviewPanelStagedApplicationResource(
                                panelInvite.getId(),
                                application.getId(),
                                application.getName(),
                                leadOrganisation.getName()
                        )
                ).getSuccess();
    }

    private ServiceResult<Organisation> getOrganisation(long organisationId) {
        return find(organisationRepository.findOne(organisationId), notFoundError(Organisation.class, organisationId));
    }

    private ServiceResult<AssessmentInterviewPanel> assignApplicationToCompetition(Application application) {
        final Role leadApplicantRole = roleRepository.findOneByName(UserRoleType.INTERVIEW_LEAD_APPLICANT.getName());
        final ProcessRole pr = new ProcessRole(application.getLeadApplicant(), application.getId(), leadApplicantRole);
        final AssessmentInterviewPanel panel = new AssessmentInterviewPanel(application, pr);

        panel.setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.ASSESSMENT_INTERVIEW_PANEL, AssessmentInterviewPanelState.CREATED.getBackingState()));
        assessmentInterviewPanelRepository.save(panel);

        return serviceSuccess(panel);
    }
}