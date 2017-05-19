package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapSet;

@Mapper(config = GlobalMapperConfig.class)
public abstract class ApplicationAssessorMapper {

    @Autowired
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    public ApplicationAssessorResource mapToResource(CompetitionParticipant competitionParticipant) {
        return mapToResource(competitionParticipant, Optional.empty());
    }

    public ApplicationAssessorResource mapToResource(CompetitionParticipant competitionParticipant, Optional<Assessment> mostRecentAssessment) {

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
        applicationAssessorResource.setAvailable(!mostRecentAssessment.isPresent());

        mostRecentAssessment.ifPresent(assessment -> {
            populateRejection(applicationAssessorResource, assessment);
            applicationAssessorResource.setMostRecentAssessmentId(assessment.getId());
            applicationAssessorResource.setMostRecentAssessmentState(assessment.getActivityState());
        });

        applicationAssessorResource.setTotalApplicationsCount(countAssignedApplications(user.getId()));
        applicationAssessorResource.setAssignedCount(countAssignedApplicationsByCompetition(competitionParticipant));
        applicationAssessorResource.setSubmittedCount(countSubmittedApplicationsByCompetition(competitionParticipant));

        return applicationAssessorResource;
    }

    private void populateRejection(ApplicationAssessorResource applicationAssessorResource, Assessment assessment) {
        if (assessment.getActivityState() == REJECTED) {
            AssessmentRejectOutcome rejection = assessment.getRejection();
            applicationAssessorResource.setRejectReason(rejection.getRejectReason());
            applicationAssessorResource.setRejectComment(rejection.getRejectComment());
        }
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
