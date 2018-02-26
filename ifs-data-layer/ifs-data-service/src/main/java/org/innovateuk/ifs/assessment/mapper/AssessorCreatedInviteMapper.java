package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite;
import org.innovateuk.ifs.invite.domain.competition.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class AssessorCreatedInviteMapper {

    private ProfileRepository profileRepository;
    private InnovationAreaMapper innovationAreaMapper;

    public AssessorCreatedInviteMapper(
            ProfileRepository profileRepository,
            InnovationAreaMapper innovationAreaMapper
    ) {
        this.profileRepository = profileRepository;
        this.innovationAreaMapper = innovationAreaMapper;
    }

    public AssessorCreatedInviteResource mapToResource(CompetitionInvite competitionInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = new AssessorCreatedInviteResource();
        assessorCreatedInvite.setName(competitionInvite.getName());
        assessorCreatedInvite.setInnovationAreas(getInnovationAreasForInvite(competitionInvite));
        assessorCreatedInvite.setCompliant(isUserCompliant(competitionInvite));
        assessorCreatedInvite.setEmail(competitionInvite.getEmail());
        assessorCreatedInvite.setInviteId(competitionInvite.getId());

        if (competitionInvite.getUser() != null) {
            assessorCreatedInvite.setId(competitionInvite.getUser().getId());
        }

        return assessorCreatedInvite;
    }

    private List<InnovationAreaResource> getInnovationAreasForInvite(CompetitionInvite competitionInvite) {
        return profileRepository.findOne(competitionInvite.getUser().getProfileId()).getInnovationAreas().stream()
                .map(innovationAreaMapper::mapToResource)
                .collect(toList());
    }

    private boolean isUserCompliant(CompetitionInvite competitionInvite) {
        if (competitionInvite == null || competitionInvite.getUser() == null) {
            return false;
        }
        Profile profile = profileRepository.findOne(competitionInvite.getUser().getProfileId());
        return profile.isCompliant(competitionInvite.getUser());
    }
}
