package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileSkillsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssessorProfileSkillsModelPopulatorTest extends BaseUnitTest{

    @InjectMocks
    private AssessorProfileSkillsModelPopulator populator;

    @Mock
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Test
    public void populateModel() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();

        UserResource user = newUserResource()
                .withId(2L)
                .withFirstName("Test")
                .withLastName("Tester")
                .withEmail("test@test.com")
                .withPhoneNumber("012345")
                .build();

        AddressResource expectedAddress = newAddressResource()
                .withAddressLine1("1 Testing Lane")
                .withTown("Testville")
                .withCounty("South Testshire")
                .withPostcode("TES TEST")
                .build();

        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);

        ProfileResource profile = newProfileResource()
                .withSkillsAreas("A Skill")
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(expectedInnovationAreas)
                .withAddress(expectedAddress)
                .build();

        AssessorProfileResource expectedProfile = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

        AssessorProfileDetailsViewModel expectedDetailsViewModel = new AssessorProfileDetailsViewModel(user, profile);

        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(expectedProfile));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(expectedDetailsViewModel);

        AssessorProfileSkillsViewModel model = populator.populateModel(user, profile, Optional.of(competition.getId()), "", false);
        AssessorProfileDetailsViewModel assessorDetails = model.getAssessorProfileDetailsViewModel();

        assertEquals("Test Tester", assessorDetails.getName());
        assertEquals("012345", assessorDetails.getPhoneNumber());
        assertEquals("A Skill", model.getSkillAreas());
        assertEquals(ACADEMIC.getDisplayName(), assessorDetails.getBusinessType());
        assertEquals("test@test.com", assessorDetails.getEmail());
        assertEquals(2, model.getInnovationAreas().size());
        assertEquals(expectedAddress, assessorDetails.getAddress());
    }
}