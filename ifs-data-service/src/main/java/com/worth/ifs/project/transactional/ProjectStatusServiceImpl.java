package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectStatusServiceImpl extends AbstractProjectServiceImpl implements ProjectStatusService {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        Competition competition = competitionRepository.findOne(competitionId);

        List<Project> projects = projectRepository.findByApplicationCompetitionId(competitionId);

        List<ProjectStatusResource> projectStatusResources = projects.stream().map(project ->
                new ProjectStatusResource(
                        project.getName(),
                        project.getId(),
                        project.getFormattedId(),
                        project.getApplication().getId(),
                        project.getApplication().getFormattedId(),
                        getProjectPartnerCount(project.getId()),
                        project.getApplication().getLeadOrganisation().getName(),
                        getProjectDetailsStatus(project),
                        getBankDetailsStatus(project),
                        getFinanceChecksStatus(project),
                        getSpendProfileStatus(project),
                        getMonitoringOfficerStatus(project),
                        getOtherDocumentsStatus(project),
                        getGrantOfferLetterStatus(project))).collect(Collectors.toList());


        CompetitionProjectsStatusResource competitionProjectsStatusResource = new CompetitionProjectsStatusResource(competition.getId(), competition.getFormattedId(), competition.getName(), projectStatusResources);

        return ServiceResult.serviceSuccess(competitionProjectsStatusResource);
    }

    private Integer getProjectPartnerCount(Long projectId){
        return getProjectUsersByProjectId(projectId).size();
    }

    private ProjectActivityStates getProjectDetailsStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getBankDetailsStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getFinanceChecksStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getSpendProfileStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getMonitoringOfficerStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getOtherDocumentsStatus(Project project){
        return ProjectActivityStates.PENDING;
    }

    private ProjectActivityStates getGrantOfferLetterStatus(Project project){
        return ProjectActivityStates.PENDING;
    }
}
