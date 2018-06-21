package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Mapper for mapping to an {@link AvailableAssessorResource}.
 */
@Component
public class AvailableAssessorMapper {

    private ProfileRepository profileRepository;
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    public AvailableAssessorMapper(
            ProfileRepository profileRepository,
            InnovationAreaMapper innovationAreaMapper) {

        this.profileRepository = profileRepository;
        this.innovationAreaMapper = innovationAreaMapper;
    }

    public AvailableAssessorResource mapToResource(CompetitionParticipant<?> participant) {
        User assessor = participant.getUser();
        Profile profile = profileRepository.findById(assessor.getProfileId()).get();

        AvailableAssessorResource availableAssessor = new AvailableAssessorResource();
        availableAssessor.setId(assessor.getId());
        availableAssessor.setEmail(assessor.getEmail());
        availableAssessor.setName(assessor.getName());
        availableAssessor.setBusinessType(profile.getBusinessType());
        availableAssessor.setCompliant(profile.isCompliant(assessor));
        availableAssessor.setInnovationAreas(simpleMap(profile.getInnovationAreas(), innovationAreaMapper::mapToResource));

        return availableAssessor;
    }
}
