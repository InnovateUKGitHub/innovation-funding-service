package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.mapper.InnovationAreaMapper;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.domain.CompetitionInvite;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
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
import static org.innovateuk.ifs.assessment.builder.AssessmentInviteBuilder.newAssessmentInvite;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteBuilder.newInterviewInvite;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssessorCreatedInviteMapperTest {

    @Mock
    private ProfileRepository profileRepositoryMock;
    @Mock
    private InnovationAreaMapper innovationAreaMapperMock;

    private AssessorCreatedInviteMapper assessorCreatedInviteMapper;

    @Before
    public void setUp() throws Exception {
        assessorCreatedInviteMapper = new AssessorCreatedInviteMapper(profileRepositoryMock, innovationAreaMapperMock);
    }

    @Test
    public void mapFromCompetitionInvite() {
        InnovationArea innovationArea = newInnovationArea().build();
        Profile profile = newProfile()
                .withInnovationArea(innovationArea)
                .build();

        CompetitionInvite invite = newInterviewInvite()
                .withEmail("test@test.com")
                .withName("Joe Bloggs")
                .withUser(
                        newUser()
                                .withProfileId(profile.getId())
                                .build()
                )
                .build();

        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);

        AssessorCreatedInviteResource assessorCreatedInviteResource = assessorCreatedInviteMapper.mapToResource(invite);

        assertThat(assessorCreatedInviteResource).isEqualToComparingFieldByField(
                newAssessorCreatedInviteResource()
                        .withId(invite.getUser().getId())
                        .withEmail(invite.getEmail())
                        .withName(invite.getName())
                        .withCompliant(profile.isCompliant(invite.getUser()))
                        .withInviteId(invite.getId())
                        .withInnovationAreas(singletonList(innovationAreaResource))
                        .build()
        );

        verify(profileRepositoryMock).findById(profile.getId());
        verify(innovationAreaMapperMock).mapToResource(innovationArea);
    }

    @Test
    public void mapFromCompetitionAssessmentInvite() {
        InnovationArea innovationArea = newInnovationArea().build();
        Profile profile = newProfile()
                .withInnovationArea(innovationArea)
                .build();

        AssessmentInvite invite = newAssessmentInvite()
                .withEmail("test@test.com")
                .withName("Joe Bloggs")
                .withUser(
                        newUser()
                                .withProfileId(profile.getId())
                                .build()
                )
                .build();

        when(profileRepositoryMock.findById(profile.getId())).thenReturn(Optional.of(profile));

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);

        AssessorCreatedInviteResource assessorCreatedInviteResource = assessorCreatedInviteMapper.mapToResource(invite);

        assertThat(assessorCreatedInviteResource).isEqualToComparingFieldByField(
                newAssessorCreatedInviteResource()
                        .withId(invite.getUser().getId())
                        .withEmail(invite.getEmail())
                        .withName(invite.getName())
                        .withCompliant(profile.isCompliant(invite.getUser()))
                        .withInviteId(invite.getId())
                        .withInnovationAreas(singletonList(innovationAreaResource))
                        .build()
        );

        verify(profileRepositoryMock).findById(profile.getId());
        verify(innovationAreaMapperMock).mapToResource(innovationArea);
    }

    @Test
    public void mapFromCompetitionAssessmentInvite_newAssessorInvite() {
        InnovationArea innovationArea = newInnovationArea().build();

        AssessmentInvite invite = newAssessmentInvite()
                .withEmail("test@test.com")
                .withName("Joe Bloggs")
                .withInnovationArea(innovationArea)
                .build();

        InnovationAreaResource innovationAreaResource = newInnovationAreaResource().build();
        when(innovationAreaMapperMock.mapToResource(innovationArea)).thenReturn(innovationAreaResource);

        AssessorCreatedInviteResource assessorCreatedInviteResource = assessorCreatedInviteMapper.mapToResource(invite);

        assertThat(assessorCreatedInviteResource).isEqualToComparingFieldByField(
                newAssessorCreatedInviteResource()
                        .withEmail(invite.getEmail())
                        .withName(invite.getName())
                        .withCompliant(false)
                        .withInviteId(invite.getId())
                        .withInnovationAreas(singletonList(innovationAreaResource))
                        .build()
        );

        verify(profileRepositoryMock, never()).findById(any());
        verify(innovationAreaMapperMock).mapToResource(innovationArea);
    }
}
