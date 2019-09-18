package org.innovateuk.ifs.assessment.profile.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileSkillsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorProfileSkillsModelPopulatorTest {

    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Mock
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void populateModel() throws Exception {
        long competitionId = 7L;
        long assessorId = 11L;
        String expectedFirstName = "firstname";
        String expectedLastName = "lastname";
        String expectedEmail = "email";
        String expectedPhone = "phone";
        String expectedSkills = "skills";
        BusinessType expectedBusinessType = ACADEMIC;
        CompetitionResource expectedCompetition = newCompetitionResource().build();
        AddressResource expectedAddress = newAddressResource().build();

        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);

        UserResource userResource = newUserResource()
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withEmail(expectedEmail)
                .withPhoneNumber(expectedPhone)
                .build();

        ProfileResource profileResource = newProfileResource()
                .withBusinessType(ACADEMIC)
                .withSkillsAreas(expectedSkills)
                .withInnovationAreas(expectedInnovationAreas)
                .withAddress(expectedAddress)
                .build();

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = new AssessorProfileDetailsViewModel(userResource, profileResource);

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(userResource)
                .withProfile(profileResource)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(expectedCompetition));
        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(assessorProfileResource));
        when(assessorProfileDetailsModelPopulator.populateModel(userResource, profileResource)).thenReturn(assessorProfileDetailsViewModel);

        AssessorProfileSkillsViewModel viewModel =
                assessorProfileSkillsModelPopulator.populateModel(assessorProfileResource.getUser(), assessorProfileResource.getProfile(), Optional.empty(), false);
        AssessorProfileDetailsViewModel assessorDetails = viewModel.getAssessorProfileDetailsViewModel();

        assertNull(viewModel.getCompetition());
        assertEquals(expectedFirstName + " " + expectedLastName, assessorDetails.getName());
        assertEquals(expectedEmail, assessorDetails.getEmail());
        assertEquals(expectedPhone, assessorDetails.getPhoneNumber());
        assertEquals(expectedSkills, viewModel.getSkillAreas());
        assertEquals(expectedAddress, assessorDetails.getAddress());
        assertEquals(expectedBusinessType.getDisplayName(), assessorDetails.getBusinessType().getDisplayName());
        assertEquals(2, viewModel.getInnovationAreas().size());
    }
}