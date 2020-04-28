package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.*;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.sort;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource.ApplicantDashboardResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInProgressRowResource.DashboardApplicationInProgressResourceBuilder;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardInSetupRowResource.DashboardInSetupRowResourceBuilder.aDashboardInSetupRowResource;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.*;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.application.resource.ApplicationState.inProgressStates;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
import static org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus.ON_HOLD;

/**
 * Transactional and secured service that generates a dashboard of applications for a user.
 */
@Service
public class ApplicationDashboardServiceImpl extends RootTransactionalService implements ApplicationDashboardService {

    @Autowired
    private InterviewAssignmentService interviewAssignmentService;
    @Autowired
    private QuestionStatusService questionStatusService;
    @Autowired
    private ApplicationRepository applicationRepository;

    @Value("${ifs.covid19.competitions}")
    private List<Long> covid19Competitions;

    @Override
    public ServiceResult<ApplicantDashboardResource> getApplicantDashboard(long userId) {
        List<Application> applications = applicationRepository.findApplicationsForDashboard(userId);

        List<DashboardInSetupRowResource> inSetup = new ArrayList<>();
        List<DashboardEuGrantTransferRowResource> euGrantTransfer = new ArrayList<>();
        List<DashboardInProgressRowResource> inProgress = new ArrayList<>();
        List<DashboardPreviousRowResource> previous = new ArrayList<>();

        applications.forEach(application -> {
            DashboardSection section = sectionForApplication(application);
            switch (section) {
                case IN_SETUP:
                    inSetup.add(toSetupResource(application, userId));
                    break;
                case EU_GRANT_TRANSFER:
                    euGrantTransfer.add(toEuGrantResource(application, userId));
                    break;
                case IN_PROGRESS:
                    inProgress.add(toInProgressResource(application, userId));
                    break;
                case PREVIOUS:
                    previous.add(toPreviousResource(application, userId));
                    break;
            }
        });

        sort(inSetup);
        sort(euGrantTransfer);
        sort(inProgress);
        sort(inSetup);

        ApplicantDashboardResource applicantDashboardResource = new ApplicantDashboardResourceBuilder()
                .withInSetup(inSetup)
                .withEuGrantTransfer(euGrantTransfer)
                .withInProgress(inProgress)
                .withPrevious(previous)
                .build();

        return serviceSuccess(applicantDashboardResource);
    }

    private DashboardSection sectionForApplication(Application application) {
        if (application.getCompetition().isH2020()) {
            return EU_GRANT_TRANSFER;
        }
        if (projectExists(application)) {
            if (projectInSetup(application)) {
                return IN_SETUP;
            } else {
                return PREVIOUS;
            }
        }
        if (applicationIsInProgress(application)) {
            if (competitionStillOpen(application)) {
                return IN_PROGRESS;
            } else {
                return PREVIOUS;
            }
        }
        if (applicantHasBeenNotifiedOfFundingDecision(application)) {
            if (application.getFundingDecision().equals(ON_HOLD)) {
                return IN_PROGRESS;
            } else {
                return PREVIOUS;
            }
        }
        if (applicationInPreviousState(application)) {
            return PREVIOUS;
        }
        return IN_PROGRESS; //Submitted and awaiting a decision.
    }

    private boolean applicationInPreviousState(Application application) {
        return INELIGIBLE_INFORMED.equals(application.getApplicationProcess().getProcessState());
    }

    private boolean competitionStillOpen(Application application) {
        return application.getCompetition().getCompetitionStatus().equals(OPEN);
    }

    private boolean applicantHasBeenNotifiedOfFundingDecision(Application application) {
        return application.getManageFundingEmailDate() != null;
    }

    private boolean applicationIsInProgress(Application application) {
        return inProgressStates.contains(application.getApplicationProcess().getProcessState());
    }

    private boolean projectExists(Application application) {
        return application.getProject() != null;
    }

    private boolean projectInSetup(Application application) {
        return !application.getProject().getProjectProcess().getProcessState().isComplete();
    }

    private DashboardPreviousRowResource toPreviousResource(Application application, long userId) {
        return new DashboardPreviousRowResource.DashboardPreviousApplicationResourceBuilder()
                .withTitle(application.getName())
                .withLeadApplicant(application.getLeadApplicant().getId().equals(userId))
                .withApplicationId(application.getId())
                .withCompetitionTitle(application.getCompetition().getName())
                .withApplicationState(application.getApplicationProcess().getProcessState())
                .withStartDate(application.getStartDate())
                .withProjectId(ofNullable(application.getProject()).map(Project::getId).orElse(null))
                .withProjectState(ofNullable(application.getProject()).map(project -> project.getProjectProcess().getProcessState()).orElse(null))
                .withCollaborationLevelSingle(!application.isCollaborativeProject())
                .build();
    }

    private DashboardInProgressRowResource toInProgressResource(Application application, long userId) {
        Optional<ProcessRole> role = application.getProcessRoles().stream()
                .filter(pr -> pr.getUser().getId().equals(userId))
                .findFirst();
        boolean invitedToInterview = interviewAssignmentService.isApplicationAssigned(application.getId()).getSuccess();

        return new DashboardApplicationInProgressResourceBuilder()
                .withTitle(application.getName())
                .withApplicationId(application.getId())
                .withCompetitionTitle(application.getCompetition().getName())
                .withAssignedToMe(isAssigned(application, role))
                .withApplicationState(application.getApplicationProcess().getProcessState())
                .withLeadApplicant(isLead(role))
                .withEndDate(application.getCompetition().getEndDate())
                .withDaysLeft(application.getCompetition().getDaysLeft())
                .withHasAssessmentStage(application.getCompetition().isHasAssessmentStage())
                .withApplicationProgress(application.getCompletion().intValue())
                .withAssignedToInterview(invitedToInterview)
                .withStartDate(application.getStartDate())
                .withShowReopenLink(showReopenLinkVisible(application, userId))
                .build();
    }

    private DashboardEuGrantTransferRowResource toEuGrantResource(Application application, long userId) {
        return new DashboardEuGrantTransferRowResource.DashboardApplicationForEuGrantTransferResourceBuilder()
                .withTitle(application.getName())
                .withApplicationId(application.getId())
                .withCompetitionTitle(application.getCompetition().getName())
                .withApplicationState(application.getApplicationProcess().getProcessState())
                .withApplicationProgress(application.getCompletion().intValue())
                .withProjectId(ofNullable(application.getProject()).map(Project::getId).orElse(null))
                .withStartDate(application.getStartDate())
                .build();
    }

    private boolean showReopenLinkVisible(Application application, long userId) {
        if (covid19Competitions.contains(application.getCompetition().getId().toString())) {
            return application.getLeadApplicant().getId().equals(userId) &&
                    CompetitionStatus.OPEN.equals(application.getCompetition().getCompetitionStatus()) &&
                    application.getFundingDecision() == null &&
                    application.isSubmitted();
        }

        return false;
    }


    private DashboardInSetupRowResource toSetupResource(Application application, long userId) {
        PartnerOrganisation partnerOrganisation = getPartnerOrganisation(application, userId);
        return aDashboardInSetupRowResource()
                .withTitle(application.getProject().getName())
                .withApplicationId(application.getId())
                .withCompetitionTitle(application.getCompetition().getName())
                .withProjectId(application.getProject().getId())
                .withProjectTitle(application.getProject().getName())
                .withTargetStartDate(application.getProject().getTargetStartDate())
                .withPendingPartner(partnerOrganisation.isPendingPartner())
                .withOrganisationId(partnerOrganisation.getOrganisation().getId())
                .build();
    }

    private PartnerOrganisation getPartnerOrganisation(Application application, long userId) {
        return application.getProject().getProjectUsers().stream()
                .filter(pu -> pu.getUser().getId().equals(userId))
                .findFirst()
                .map(ProjectUser::getPartnerOrganisation)
                .orElseThrow(ObjectNotFoundException::new);
    }

    private boolean isAssigned(Application application, Optional<ProcessRole> processRole) {
        if (processRole.isPresent() && !isLead(processRole)) {
            int count = questionStatusService.getCountByApplicationIdAndAssigneeId(application.getId(), processRole.get().getId()).getSuccess();
            return count != 0;
        } else {
            return false;
        }
    }

    private boolean isLead(Optional<ProcessRole> processRole) {
        return processRole.map(ProcessRole::getRole).map(Role::isLeadApplicant).orElse(false);
    }
}
