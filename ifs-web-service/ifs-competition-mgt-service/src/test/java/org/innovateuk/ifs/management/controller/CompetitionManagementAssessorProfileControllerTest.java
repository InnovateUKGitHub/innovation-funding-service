package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.AssessorProfileModelPopulator;
import org.innovateuk.ifs.management.viewmodel.AssessorsProfileViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementAssessorProfileControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessorProfileController> {

    @Spy
    @InjectMocks
    private AssessorProfileModelPopulator assessorProfileModelPopulator;

    private CompetitionResource competition;

    @Override
    protected CompetitionManagementAssessorProfileController supplyControllerUnderTest() {
        return new CompetitionManagementAssessorProfileController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();
    }

    @Test
    public void profile() throws Exception {
        Long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}", competition.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andReturn();

        AssessorsProfileViewModel model = (AssessorsProfileViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Test Tester", model.getName());
        assertEquals("012345", model.getPhone());
        assertEquals("A Skill", model.getSkills());
        assertEquals(ACADEMIC.getDisplayName(), model.getBusinessType());
        assertEquals("test@test.com", model.getEmail());
        assertEquals(2, model.getInnovationSectors().size());
        assertEquals(expectedAddress, model.getAddress());

        verify(assessorRestService, only()).getAssessorProfile(assessorId);
    }

    @Test
    public void displayAssessorProfile_backUrlPreservesQueryParams() throws Exception {
        Long assessorId = 1L;
        Long applicationId = 2L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        String expectedBackUrl = "/assessment/competition/" + competition.getId() + "/application/" + applicationId + "/assessors?param1=abc&param2=def%26ghi";

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}", competition.getId(), assessorId)
                .param("param1", "abc")
                .param("param2", "def&ghi")
                .param("applicationId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/profile"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayAssessorProfile_AssessorFindOrigin() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(COMP_ADMIN, PROJECT_FINANCE).build())).build());
        Long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        String expectedBackUrl = "/competition/" + competition.getId() + "/assessors/find";

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}", competition.getId(), assessorId)
                .param("origin", "ASSESSOR_FIND"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/profile"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    private AddressResource getExpectedAddress() {
        return newAddressResource()
                .withAddressLine1("1 Testing Lane")
                .withTown("Testville")
                .withCounty("South Testshire")
                .withPostcode("TES TEST")
                .build();
    }

    private List<InnovationAreaResource> getInnovationAreas() {
        return newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);
    }

    private AssessorProfileResource getAssessorProfile(AddressResource expectedAddress, List<InnovationAreaResource> expectedInnovationAreas) {
        return newAssessorProfileResource()
                .withUser(
                        newUserResource()
                                .withFirstName("Test")
                                .withLastName("Tester")
                                .withEmail("test@test.com")
                                .withPhoneNumber("012345")
                                .build()
                )
                .withProfile(
                        newProfileResource()
                                .withSkillsAreas("A Skill")
                                .withBusinessType(ACADEMIC)
                                .withInnovationAreas(expectedInnovationAreas)
                                .withAddress(expectedAddress)
                                .build()
                )
                .build();
    }
}
