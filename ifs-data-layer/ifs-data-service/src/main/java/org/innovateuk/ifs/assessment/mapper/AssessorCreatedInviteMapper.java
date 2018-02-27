package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.domain.competition.CompetitionAssessmentInvite;
import org.innovateuk.ifs.invite.domain.competition.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Component
public class AssessorCreatedInviteMapper {

    private ProfileRepository profileRepository;
    private InnovationAreaMapper innovationAreaMapper;

    @Autowired
    public AssessorCreatedInviteMapper(
            ProfileRepository profileRepository,
            InnovationAreaMapper innovationAreaMapper
    ) {
        this.profileRepository = profileRepository;
        this.innovationAreaMapper = innovationAreaMapper;
    }

    public AssessorCreatedInviteResource mapToResource(CompetitionAssessmentInvite competitionAssessmentInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = mapBaseProperties(competitionAssessmentInvite);

        if (competitionAssessmentInvite.isNewAssessorInvite()) {
            assessorCreatedInvite.setInnovationAreas(
                    singletonList(innovationAreaMapper.mapToResource(competitionAssessmentInvite.getInnovationArea()))
            );
        } else {
            assessorCreatedInvite.setInnovationAreas(getUserInnovationAreas(competitionAssessmentInvite.getUser()));
        }

        return assessorCreatedInvite;
    }

    public AssessorCreatedInviteResource mapToResource(CompetitionInvite<?> competitionInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = mapBaseProperties(competitionInvite);
        assessorCreatedInvite.setInnovationAreas(getUserInnovationAreas(competitionInvite.getUser()));

        return assessorCreatedInvite;
    }

    private AssessorCreatedInviteResource mapBaseProperties(CompetitionInvite<?> competitionInvite) {
        AssessorCreatedInviteResource assessorCreatedInvite = new AssessorCreatedInviteResource();
        assessorCreatedInvite.setName(competitionInvite.getName());
        assessorCreatedInvite.setCompliant(isUserCompliant(competitionInvite));
        assessorCreatedInvite.setEmail(competitionInvite.getEmail());
        assessorCreatedInvite.setInviteId(competitionInvite.getId());

        if (competitionInvite.getUser() != null) {
            assessorCreatedInvite.setId(competitionInvite.getUser().getId());
        }

        return assessorCreatedInvite;
    }

    private List<InnovationAreaResource> getUserInnovationAreas(User user) {
        return profileRepository.findOne(user.getProfileId()).getInnovationAreas().stream()
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
