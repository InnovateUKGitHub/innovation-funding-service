package org.innovateuk.ifs.management.competition.inflight.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessor.controller.CompetitionManagementAssessorProfileController;
import org.innovateuk.ifs.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.FAMILY_FINANCIAL;
import static org.innovateuk.ifs.user.resource.AffiliationType.PROFESSIONAL;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementAssessorProfileControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessorProfileController> {

    @Spy
    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Mock
    private AssessorRestService assessorRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AffiliationRestService affiliationRestService;

    private CompetitionResource competition;

    @Override
    protected CompetitionManagementAssessorProfileController supplyControllerUnderTest() {
        return new CompetitionManagementAssessorProfileController();
    }

    @Before
    public void setUpCompetition() {

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();

        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competition));
    }

    @Test
    public void assessorProfileSkills() throws Exception {
        long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}?tab=skills", competition.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("profile/skills"));

        verify(assessorRestService, only()).getAssessorProfile(assessorId);
    }

    @Test
    public void assessorProfileDeclaration() throws Exception {
        long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(newAffiliationResource()
                        .withId(null, null)
                        .withAffiliationType(PROFESSIONAL, FAMILY_FINANCIAL)
                        .withExists(true, true)
                        .withUser(null, null)
                        .build(2)
                )
                .build();

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));
        when(affiliationRestService.getUserAffiliations(anyLong())).thenReturn(restSuccess(affiliationListResource));

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}?tab=declaration", competition.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("profile/declaration-of-interest"));

        verify(assessorRestService).getAssessorProfile(assessorId);
        verify(affiliationRestService).getUserAffiliations(anyLong());
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