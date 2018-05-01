package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.competition.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * Mapper for mapping to an {@link AssessorCreatedInviteResource}.
 */
@Component
public class AssessorCreatedInviteMapper {

    private ProfileRepository profileRepository;
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    public AssessorCreatedInviteMapper(
            ProfileRepository profileRepository,
            InnovationAreaMapper innovationAreaMapper) {
        
        this.profileRepository = profileRepository;
        this.innovationAreaMapper = innovationAreaMapper;
    }

    public AssessorCreatedInviteResource mapToResource(AssessmentInvite competitionAssessmentInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = mapBaseProperties(competitionAssessmentInvite);

        if (competitionAssessmentInvite.isNewAssessorInvite()) {
            assessorCreatedInvite.setInnovationAreas(
                    singletonList(innovationAreaMapper.mapToResource(competitionAssessmentInvite.getInnovationArea()))
            );

            return assessorCreatedInvite;
        }

        return mapUserProperties(competitionAssessmentInvite, assessorCreatedInvite);
    }

    public AssessorCreatedInviteResource mapToResource(CompetitionInvite<?> competitionInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = mapBaseProperties(competitionInvite);

        if (competitionInvite.getUser() != null) {
            return mapUserProperties(competitionInvite, assessorCreatedInvite);
        }

        return assessorCreatedInvite;
    }

    private AssessorCreatedInviteResource mapBaseProperties(CompetitionInvite<?> competitionInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = new AssessorCreatedInviteResource();
        assessorCreatedInvite.setName(competitionInvite.getName());
        assessorCreatedInvite.setEmail(competitionInvite.getEmail());
        assessorCreatedInvite.setInviteId(competitionInvite.getId());

        return assessorCreatedInvite;
    }

    private AssessorCreatedInviteResource mapUserProperties(
            CompetitionInvite<?> competitionInvite,
            AssessorCreatedInviteResource assessorCreatedInvite
    ) {
        if (competitionInvite.getUser() != null) {
            Profile profile = profileRepository.findOne(competitionInvite.getUser().getProfileId());

            assessorCreatedInvite.setId(competitionInvite.getUser().getId());
            assessorCreatedInvite.setInnovationAreas(mapInnovationAreas(profile));
            assessorCreatedInvite.setCompliant(profile.isCompliant(competitionInvite.getUser()));
        }

        return assessorCreatedInvite;
    }

    private List<InnovationAreaResource> mapInnovationAreas(Profile profile) {
        return profile.getInnovationAreas().stream()
                .map(innovationAreaMapper::mapToResource)
                .collect(toList());
    }
}
