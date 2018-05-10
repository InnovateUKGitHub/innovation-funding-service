package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class GoogleAnalyticsDataLayerServiceImpl extends BaseTransactionalService implements GoogleAnalyticsDataLayerService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Override
    public ServiceResult<String> getCompetitionNameByApplicationId(long applicationId) {
        return getApplication(applicationId)
                .andOnSuccess(application -> getCompetition(application.getCompetition().getId())
                        .andOnSuccessReturn(Competition::getName));
    }

    @Override
    public ServiceResult<String> getCompetitionName(long competitionId) {
        return find(getCompetition(competitionId))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByProjectId(long projectId) {
        Application application = applicationRepository.findByProjectId(projectId);

        return find(getCompetition(application.getCompetition().getId()))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId) {
        Application application = applicationRepository.findByAssessmentId(assessmentId);
        return find(getCompetition(application.getCompetition().getId()))
                .andOnSuccessReturn(Competition::getName);
    }

    @Override
    public ServiceResult<List<Role>> getRolesByApplicationIdForCurrentUser(long applicationId) {

        return getCurrentlyLoggedInUser().andOnSuccessReturn(
                user -> simpleMap(
                        processRoleRepository.findByUserAndApplicationId(user, applicationId),
                        ProcessRole::getRole
                ));
    }

    @Override
    public ServiceResult<List<Role>> getRolesByProjectIdForCurrentUser(long projectId) {

        return getCurrentlyLoggedInUser().andOnSuccessReturn(
                user -> simpleMap(
                        projectUserRepository.findByProjectIdAndUserId(projectId, user.getId()),
                        projectUser -> Role.getById(projectUser.getRole().getId())
                ));

    }
}