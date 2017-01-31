package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.repository.CompetitionParticipantRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.invite.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;
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
    private ProfileRepository profileRepository;

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Override
    public ServiceResult<List<ApplicationAssessorResource>> getAssessors(Long applicationId) {
        return find(applicationRepository.findOne(applicationId), notFoundError(Application.class, applicationId)).andOnSuccessReturn(application ->
                simpleMap(competitionParticipantRepository.getByCompetitionIdAndRoleAndStatus(application.getCompetition().getId(), ASSESSOR, ParticipantStatus.ACCEPTED),
                        competitionParticipant -> getApplicationAssessor(competitionParticipant, applicationId))
        );
    }

    @Override
    public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(application -> {
            Competition competition = application.getCompetition();
            return new ApplicationAssessmentSummaryResource(application.getId(),
                    application.getName(),
                    competition.getId(),
                    competition.getName(),
                    getLeadOrganisationName(application),
                    getPartnerOrganisationNames(application));
        });
    }

    private List<String> getPartnerOrganisationNames(Application application) {
        return application.getProcessRoles().stream()
                .filter(ProcessRole::isCollaborator)
                .map(processRole -> organisationRepository.findOne(processRole.getOrganisationId()).getName())
                .sorted(Collator.getInstance())
                .collect(toList());
    }

    private String getLeadOrganisationName(Application application) {
        return application.getProcessRoles().stream()
                .filter(ProcessRole::isLeadApplicant)
                .findFirst()
                .map(processRole -> organisationRepository.findOne(processRole.getOrganisationId()).getName())
                .orElse(StringUtils.EMPTY);
    }

    private ApplicationAssessorResource getApplicationAssessor(CompetitionParticipant competitionParticipant, Long applicationId) {
        Optional<Assessment> mostRecentAssessment = getMostRecentAssessment(competitionParticipant, applicationId);
        Optional<ProcessOutcome> rejectedOutcome = getRejectedOutcome(mostRecentAssessment);

        User user = competitionParticipant.getUser();
        Optional<Profile> profile = ofNullable(profileRepository.findOne(user.getProfileId()));

        Set<InnovationAreaResource> innovationAreas = simpleMapSet(profile.map(Profile::getInnovationAreas)
                .orElse(emptySet()), innovationAreaMapper::mapToResource);

        ApplicationAssessorResource applicationAssessorResource = new ApplicationAssessorResource();
        applicationAssessorResource.setUserId(user.getId());
        applicationAssessorResource.setFirstName(user.getFirstName());
        applicationAssessorResource.setLastName(user.getLastName());
        applicationAssessorResource.setBusinessType(profile.map(Profile::getBusinessType).orElse(null));
        applicationAssessorResource.setInnovationAreas(innovationAreas);
        applicationAssessorResource.setSkillAreas(profile.map(Profile::getSkillsAreas).orElse(null));
        applicationAssessorResource.setRejectReason(rejectedOutcome.map(ProcessOutcome::getDescription).orElse(null));
        applicationAssessorResource.setRejectComment(rejectedOutcome.map(ProcessOutcome::getComment).orElse(null));
        applicationAssessorResource.setAvailable(!mostRecentAssessment.isPresent());
        applicationAssessorResource.setMostRecentAssessmentId(mostRecentAssessment.map(Assessment::getId).orElse(null));
        applicationAssessorResource.setMostRecentAssessmentState(mostRecentAssessment.map(Assessment::getActivityState).orElse(null));
        applicationAssessorResource.setTotalApplicationsCount(countAssignedApplications(user.getId()));
        applicationAssessorResource.setAssignedCount(countAssignedApplicationsByCompetition(competitionParticipant));
        applicationAssessorResource.setSubmittedCount(countSubmittedApplicationsByCompetition(competitionParticipant));

        return applicationAssessorResource;
    }

    private Optional<Assessment> getMostRecentAssessment(CompetitionParticipant competitionParticipant, Long applicationId) {
        return assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(competitionParticipant.getUser().getId(), applicationId);
    }

    private Optional<ProcessOutcome> getRejectedOutcome(Optional<Assessment> assessment) {
        return assessment.flatMap(value -> value.getLastOutcome(AssessmentOutcomes.REJECT));
    }

    private long countAssignedApplications(Long userId) {
        return assessmentRepository.countByParticipantUserIdAndActivityStateStateNotIn(userId, getBackingStates(of(REJECTED, WITHDRAWN)));
    }

    private long countAssignedApplicationsByCompetition(CompetitionParticipant competitionParticipant) {
        return countAssessmentsByCompetitionParticipantInStates(competitionParticipant, complementOf(of(REJECTED, WITHDRAWN)));
    }

    private long countSubmittedApplicationsByCompetition(CompetitionParticipant competitionParticipant) {
        return countAssessmentsByCompetitionParticipantInStates(competitionParticipant, of(SUBMITTED));
    }

    private long countAssessmentsByCompetitionParticipantInStates(CompetitionParticipant competitionParticipant, Set<AssessmentStates> states) {
        return assessmentRepository.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(competitionParticipant.getUser().getId(),
                competitionParticipant.getProcess().getId(),
                getBackingStates(states));
    }
}
