package com.worth.ifs.project.transactional;

import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.transactional.ProjectFinanceService;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.project.users.ProjectUsersHelper;
import com.worth.ifs.user.domain.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectStatusServiceImpl extends AbstractProjectServiceImpl implements ProjectStatusService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        Competition competition = competitionRepository.findOne(competitionId);

        List<Project> projects = projectRepository.findByApplicationCompetitionId(competitionId);

        List<ProjectStatusResource> projectStatusResources = simpleMap(projects, project -> getProjectStatusResourceByProject(project));

        CompetitionProjectsStatusResource competitionProjectsStatusResource = new CompetitionProjectsStatusResource(competition.getId(), competition.getFormattedId(), competition.getName(), projectStatusResources);

        return ServiceResult.serviceSuccess(competitionProjectsStatusResource);
    }

    @Override
    public ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        if(null != project) {
            return ServiceResult.serviceSuccess(getProjectStatusResourceByProject(project));
        }
        return ServiceResult.serviceFailure(new Error(GENERAL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private ProjectStatusResource getProjectStatusResourceByProject(Project project) {
        ProjectActivityStates projectDetailsStatus = getProjectDetailsStatus(project);
        return new ProjectStatusResource(
                project.getName(),
                project.getId(),
                project.getFormattedId(),
                project.getApplication().getId(),
                project.getApplication().getFormattedId(),
                getProjectPartnerCount(project.getId()),
                null != project.getApplication().getLeadOrganisation() ? project.getApplication().getLeadOrganisation().getName() : "",
                getProjectDetailsStatus(project),
                getBankDetailsStatus(project),
                getFinanceChecksStatus(project),
                getSpendProfileStatus(project),
                getMonitoringOfficerStatus(project, projectDetailsStatus),
                getOtherDocumentsStatus(project),
                getGrantOfferLetterStatus(project));
    }

    private Integer getProjectPartnerCount(Long projectId){
        return projectUsersHelper.getPartnerOrganisations(projectId).size();
    }

    private ProjectActivityStates getProjectDetailsStatus(Project project){
        return createProjectDetailsStatus(project);
    }

    private ProjectActivityStates getBankDetailsStatus(Project project){
        // Show hourglass when there is at least one org which hasn't submitted bank details but is required to.
        for(Organisation organisation : project.getOrganisations()){
            Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId()));
            ProjectActivityStates organisationBankDetailsStatus = createBankDetailStatus(project, bankDetails, organisation);
            if(!bankDetails.isPresent() && !organisationBankDetailsStatus.equals(NOT_REQUIRED)){
                return PENDING;
            }
        }

        // Show action required by internal user (pending flag) when all bank details submitted but at least one requires manual approval.
        for(Organisation organisation : project.getOrganisations()){
            Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), organisation.getId()));
            ProjectActivityStates organisationBankDetailsStatus = createBankDetailStatus(project, bankDetails, organisation);
            if(bankDetails.isPresent() && organisationBankDetailsStatus.equals(PENDING)){
                return ACTION_REQUIRED;
            }
        }

        // otherwise show a tick
        return COMPLETE;
    }

    private ProjectActivityStates getFinanceChecksStatus(Project project){
        return ACTION_REQUIRED;
    }

    private ProjectActivityStates getSpendProfileStatus(Project project){
        List<Organisation> organisations = project.getOrganisations();

        ApprovalType approvalType = projectFinanceService.getSpendProfileStatusByProjectId(project.getId()).getSuccessObject();
        if(ApprovalType.APPROVED.equals(approvalType)) {
            return COMPLETE;
        } else if(ApprovalType.REJECTED.equals(approvalType)) {
            return PENDING;
        }

        if (project.getSpendProfileSubmittedDate() != null) {
            return COMPLETE;
        }

        for(Organisation organisation : organisations) {
            Optional<SpendProfile> spendProfile = spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), organisation.getId());

            ProjectActivityStates financeChecksStatus = ACTION_REQUIRED;
            if (spendProfile.isPresent()) {
                if(!financeChecksStatus.equals(COMPLETE)){
                    return NOT_STARTED;
                } else {
                    ProjectActivityStates orgSPStatus = createSpendProfileStatus(financeChecksStatus, spendProfile);
                    if (orgSPStatus != COMPLETE) {
                        return PENDING;
                    }
                }
            }
        }

        return NOT_STARTED;
    }

    private ProjectActivityStates getMonitoringOfficerStatus(Project project, ProjectActivityStates projectDetailsStatus){
        return createMonitoringOfficerStatus(getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject(), projectDetailsStatus);
    }

    private ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    private ProjectActivityStates getOtherDocumentsStatus(Project project){
        return createOtherDocumentStatus(project);
    }

    private ProjectActivityStates getGrantOfferLetterStatus(Project project){
        if(project.getOfferSubmittedDate() != null) {
            return ACTION_REQUIRED;
        }
        return NOT_STARTED;
    }
}
