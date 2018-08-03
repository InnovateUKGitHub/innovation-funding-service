package org.innovateuk.ifs.management.controller;

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
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileSkillsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
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
    private String expectedPrincipalEmployer;
    private String expectedRole;
    private String expectedProfessionalAffiliations;
    private String expectedFinancialInterests;
    private String expectedFamilyFinancialInterests;
    private List<AffiliationResource> expectedFamilyAffiliations;
    private List<AffiliationResource> expectedAppointments;
    private AffiliationResource principalEmployer;
    private AffiliationResource professionalAffiliations;
    private AffiliationResource financialInterests;
    private AffiliationResource familyFinancialInterests;

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

        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competition));
    }

    @Test
    public void assessorProfileSkills() throws Exception {
        long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}/skills", competition.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andReturn();

        AssessorProfileSkillsViewModel model = (AssessorProfileSkillsViewModel) result.getModelAndView().getModel().get("model");
        AssessorProfileDetailsViewModel assessorDetails = model.getAssessorProfileDetailsViewModel();

        assertEquals("Test Tester", assessorDetails.getName());
        assertEquals("012345", assessorDetails.getPhoneNumber());
        assertEquals("A Skill", model.getSkillAreas());
        assertEquals(ACADEMIC.getDisplayName(), assessorDetails.getBusinessType());
        assertEquals("test@test.com", assessorDetails.getEmail());
        assertEquals(2, model.getInnovationAreas().size());
        assertEquals(expectedAddress, assessorDetails.getAddress());

        verify(assessorRestService, only()).getAssessorProfile(assessorId);
    }

    @Test
    public void assessorProfileDeclaration() throws Exception {
        long assessorId = 1L;

        setupAffiliations();

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}/declaration", competition.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andReturn();

        AssessorProfileDeclarationViewModel model = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");
        AssessorProfileDetailsViewModel assessorDetails = model.getAssessorProfileDetailsViewModel();

        assertEquals("Test Tester", assessorDetails.getName());
        assertEquals("012345", assessorDetails.getPhoneNumber());
        assertEquals(ACADEMIC.getDisplayName(), assessorDetails.getBusinessType());
        assertEquals("test@test.com", assessorDetails.getEmail());
        assertEquals(expectedAddress, assessorDetails.getAddress());
        assertEquals(expectedAppointments, model.getAppointments());
        assertEquals(expectedFamilyAffiliations, model.getFamilyAffiliations());
        assertEquals(expectedFamilyFinancialInterests, model.getFamilyFinancialInterests());
        assertEquals(expectedFinancialInterests, model.getFinancialInterests());
        assertEquals(expectedPrincipalEmployer, model.getPrincipalEmployer());
        assertEquals(expectedProfessionalAffiliations, model.getProfessionalAffiliations());
        assertEquals(expectedRole, model.getRole());

        verify(assessorRestService).getAssessorProfile(assessorId);
        verify(affiliationRestService).getUserAffiliations(anyLong());
    }

    @Test
    public void displayAssessorProfile_backUrlPreservesQueryParams() throws Exception {
        Long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        String expectedBackUrl = "/competition/" + competition.getId() + "/assessors/find?param1=abc&param2=def%26ghi";

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}/skills", competition.getId(), assessorId)
                .param("param1", "abc")
                .param("param2", "def&ghi")
                .param("applicationId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/profile-skills"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayAssessorProfileAsCompAdmin_AssessorFindOrigin() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());

        Long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        String expectedBackUrl = "/competition/" + competition.getId() + "/assessors/find";

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}/skills", competition.getId(), assessorId)
                .param("origin", "ASSESSOR_FIND"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/profile-skills"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayAssessorProfileAsProjectFinance_AssessorFindOrigin() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());

        Long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        when(assessorRestService.getAssessorProfile(assessorId)).thenReturn(restSuccess(expectedProfile));

        String expectedBackUrl = "/competition/" + competition.getId() + "/assessors/find";

        mockMvc.perform(get("/competition/{competitionId}/assessors/profile/{assessorId}/skills", competition.getId(), assessorId)
                .param("origin", "ASSESSOR_FIND"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/profile-skills"))
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

    private void setupAffiliations() {

         expectedPrincipalEmployer = "Big Name Corporation";
         expectedRole = "Financial Accountant";
         expectedProfessionalAffiliations = "Professional affiliations...";
         expectedFinancialInterests = "Other financial interests...";
         expectedFamilyFinancialInterests = "Other family financial interests...";

        expectedAppointments = newAffiliationResource()
                .withAffiliationType(PERSONAL)
                .withOrganisation("Org 1", "Org 2")
                .withPosition("Pos 1", "Post 2")
                .withExists(TRUE)
                .build(2);
        expectedFamilyAffiliations = newAffiliationResource()
                .withAffiliationType(FAMILY)
                .withRelation("Relation 1", "Relation 2")
                .withOrganisation("Org 1", "Org 2")
                .withExists(TRUE)
                .build(2);
        principalEmployer = newAffiliationResource()
                .withAffiliationType(EMPLOYER)
                .withExists(TRUE)
                .withOrganisation(expectedPrincipalEmployer)
                .withPosition(expectedRole)
                .build();
        professionalAffiliations = newAffiliationResource()
                .withAffiliationType(PROFESSIONAL)
                .withExists(TRUE)
                .withDescription(expectedProfessionalAffiliations)
                .build();
        financialInterests = newAffiliationResource()
                .withAffiliationType(PERSONAL_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFinancialInterests)
                .build();
        familyFinancialInterests = newAffiliationResource()
                .withAffiliationType(FAMILY_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFamilyFinancialInterests)
                .build();

        when(affiliationRestService.getUserAffiliations(anyLong()))
                .thenReturn(restSuccess(new AffiliationListResource(combineLists(
                        combineLists(
                                expectedAppointments,
                                expectedFamilyAffiliations
                        ),
                        principalEmployer,
                        professionalAffiliations,
                        financialInterests,
                        familyFinancialInterests
                )
                )));
    }

}
