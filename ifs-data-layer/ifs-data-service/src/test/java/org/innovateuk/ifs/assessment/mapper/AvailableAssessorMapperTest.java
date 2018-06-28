package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static freemarker.template.utility.Collections12.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.review.builder.ReviewParticipantBuilder.newReviewParticipant;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailableAssessorMapperTest {

    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private InnovationAreaMapper innovationAreaMapper;

    private AvailableAssessorMapper availableAssessorMapper;

    @Before
    public void setUp() throws Exception {
        availableAssessorMapper = new AvailableAssessorMapper(profileRepository, innovationAreaMapper);
    }

    @Test
    public void mapToResource() {
        InnovationArea innovationArea = newInnovationArea().build();

        Profile profile = newProfile()
                .withInnovationArea(innovationArea)
                .build();

        ReviewParticipant competitionParticipant = newReviewParticipant()
                .withUser(
                        newUser()
                                .withFirstName("Joe")
                                .withLastName("Bloggs")
                                .withEmailAddress("test@test.com")
                                .withProfileId(profile.getId())
                                .build()
                )
                .build();

        when(profileRepository.findById(profile.getId())).thenReturn(Optional.of(profile));

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();
        when(innovationAreaMapper.mapToResource(innovationArea)).thenReturn(innovationAreaResource);

        AvailableAssessorResource availableAssessorResource =
                availableAssessorMapper.mapToResource(competitionParticipant);

        assertThat(availableAssessorResource).isEqualToIgnoringGivenFields(
                newAvailableAssessorResource()
                        .withId(competitionParticipant.getUser().getId())
                        .withEmail(competitionParticipant.getUser().getEmail())
                        .withName(competitionParticipant.getUser().getName())
                        .withInnovationAreas(singletonList(innovationAreaResource))
                        .withBusinessType(profile.getBusinessType())
                        .withCompliant(profile.isCompliant(competitionParticipant.getUser()))
                        .build()
        );

        verify(profileRepository).findById(profile.getId());
        verify(innovationAreaMapper).mapToResource(innovationArea);
    }
}
