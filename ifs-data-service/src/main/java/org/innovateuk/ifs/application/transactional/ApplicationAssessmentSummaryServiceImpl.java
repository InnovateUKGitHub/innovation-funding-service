package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}'s.
 */
@Service
public class ApplicationAssessmentSummaryServiceImpl extends BaseTransactionalService implements ApplicationAssessmentSummaryService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private CompetitionParticipantRepository competitionParticipantRepository;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Override
    public ServiceResult<List<ApplicationAssessorResource>> getAssessors(Long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId)).andOnSuccessReturn(application ->
                simpleMap(competitionParticipantRepository.getByCompetitionIdAndRole(1L, ASSESSOR), competitionParticipant -> getApplicationAssessor(competitionParticipant))
        );
    }

    @Override
    public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn((Application application) -> {
            Competition competition = application.getCompetition();
            ApplicationAssessmentSummaryResource applicationAssessmentSummaryResource = new ApplicationAssessmentSummaryResource(application.getId(),
                    application.getName(),
                    competition.getId(),
                    competition.getName(),
                    getPartnerOrganisationNames(application));
            return applicationAssessmentSummaryResource;
        });
    }

    private List<String> getPartnerOrganisationNames(Application application) {
        return application.getProcessRoles().stream()
                .filter(processRole ->
                        processRole.isLeadApplicant() || processRole.isCollaborator())
                .map(ProcessRole::getOrganisation)
                .map(Organisation::getName)
                .collect(toList());
    }

    private ApplicationAssessorResource getApplicationAssessor(CompetitionParticipant competitionParticipant) {
        User user = competitionParticipant.getUser();
        Profile profile = user.getProfile();
        ApplicationAssessorResource applicationAssessorResource = new ApplicationAssessorResource();
        applicationAssessorResource.setFirstName(user.getFirstName());
        applicationAssessorResource.setLastName(user.getLastName());
        applicationAssessorResource.setBusinessType(profile.getBusinessType());
        applicationAssessorResource.setInnovationAreas(simpleMap(user.getInnovationAreas(), innovationAreaMapper::mapToResource));
        applicationAssessorResource.setSkillAreas(profile.getSkillsAreas());
        return applicationAssessorResource;
    }

    private AssessmentStates getMostRecentAssessmentState(Long applicationId, Long userId) {
        // TODO
        return AssessmentStates.PENDING;
    }

    private boolean isAvailableToAssess(Long applicationId, Long userId) {
        // get all assessments
        // TODO true if user has no non-rejected assessments for this application, otherwise false.
        return false;
    }

    private int countAssignedApplications(Long userId) {
        // TODO count applications for ALL competitions that are in assessment where exists an assessment not REJECTED or WITHDRAWN
        return 0;
    }

    private int countAssignedApplications(Long userId, Long competitionId) {
        return countApplicationsByCompetitionAndUserAndAssessmentStates(userId, competitionId, complementOf(of(REJECTED, WITHDRAWN)));
    }

    private int countSubmittedApplications(Long userId, Long competitionId) {
        return countApplicationsByCompetitionAndUserAndAssessmentStates(userId, competitionId, of(SUBMITTED));
    }

    private int countApplicationsByCompetitionAndUserAndAssessmentStates(Long userId, Long competitionId, Set<AssessmentStates> states) {
        // TODO count applications of the specified competition, assessed by the specified user, where exists assessments in the specified states
        List<Assessment> assessments = assessmentRepository.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(
                userId, competitionId
        );
        return 0;
    }
}