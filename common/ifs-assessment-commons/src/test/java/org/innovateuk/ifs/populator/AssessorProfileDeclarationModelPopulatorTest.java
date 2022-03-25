package org.innovateuk.ifs.populator;


import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.EDIStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.TimeMachine;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorProfileDeclarationModelPopulatorTest {

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
    private AssessorProfileResource expectedProfile;
    private AddressResource expectedAddress;
    private List<InnovationAreaResource> expectedInnovationAreas;
    private ProfileResource profile;
    private UserResource user;
    private AssessorProfileDetailsViewModel expectedDetailsViewModel;
    @InjectMocks
    private AssessorProfileDeclarationModelPopulator populator;

    @Mock
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Mock
    private AffiliationRestService affiliationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessorRestService assessorRestService;
    private final ZonedDateTime fixedClock = ZonedDateTime.parse("2021-10-12T09:38:12.850Z");


    @Before
    public void setup() {
        TimeMachine.useFixedClockAt(fixedClock);
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // start with fresh ids when using builders
        clearUniqueIds();
    }


    @Test
    public void populateModelAssessorRole() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();
        ReflectionTestUtils.setField(populator, "isEdiUpdateEnabled", true);
        user = newUserResource()
                .withId(2L)
                .withFirstName("Test")
                .withLastName("Tester")
                .withEmail("test@test.com")
                .withPhoneNumber("012345")
                .withEdiStatusReviewDate(fixedClock)
                .withEdiStatus(EDIStatus.COMPLETE)
                .withRolesGlobal(Collections.singletonList(ASSESSOR))
                .build();
        setupAffiliations();
        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(expectedProfile));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(expectedDetailsViewModel);

        AssessorProfileDeclarationViewModel model = populator.populateModel(user, profile, Optional.of(competition.getId()), false);
        AssessorProfileDetailsViewModel assessorDetails = model.getAssessorProfileDetailsViewModel();

        assertEquals("Test Tester", assessorDetails.getName());
        assertEquals("012345", assessorDetails.getPhoneNumber());
        assertEquals(ACADEMIC.getDisplayName(), assessorDetails.getBusinessType().getDisplayName());
        assertEquals("test@test.com", assessorDetails.getEmail());
        assertEquals(expectedAddress, assessorDetails.getAddress());
        assertEquals(expectedAppointments, model.getAppointments());
        assertEquals(expectedFamilyAffiliations, model.getFamilyAffiliations());
        assertEquals(expectedFamilyFinancialInterests, model.getFamilyFinancialInterests());
        assertEquals(expectedFinancialInterests, model.getFinancialInterests());
        assertEquals(expectedPrincipalEmployer, model.getPrincipalEmployer());
        assertEquals(expectedProfessionalAffiliations, model.getProfessionalAffiliations());
        assertEquals(expectedRole, model.getRole());
        assertEquals(fixedClock, model.getEdiReviewDate());
        assertEquals(EDIStatus.COMPLETE, model.getEdiStatus());
        assertFalse( model.isEdiUpdateEnabled());

    }

    @Test
    public void populateModeApplicantAndAssessorRole() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withId(1L)
                .build();
        ReflectionTestUtils.setField(populator, "isEdiUpdateEnabled", true);

        user = newUserResource()
                .withId(2L)
                .withFirstName("Test")
                .withLastName("Tester")
                .withEmail("test@test.com")
                .withPhoneNumber("012345")
                .withEdiStatusReviewDate(fixedClock)
                .withEdiStatus(EDIStatus.INCOMPLETE)
                .withRolesGlobal(Arrays.asList(ASSESSOR, APPLICANT))
                .build();
        setupAffiliations();
        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(expectedProfile));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(expectedDetailsViewModel);

        AssessorProfileDeclarationViewModel model = populator.populateModel(user, profile, Optional.of(competition.getId()), false);
        AssessorProfileDetailsViewModel assessorDetails = model.getAssessorProfileDetailsViewModel();

        assertEquals("Test Tester", assessorDetails.getName());
        assertEquals("012345", assessorDetails.getPhoneNumber());
        assertEquals(ACADEMIC.getDisplayName(), assessorDetails.getBusinessType().getDisplayName());
        assertEquals("test@test.com", assessorDetails.getEmail());
        assertEquals(expectedAddress, assessorDetails.getAddress());
        assertEquals(expectedAppointments, model.getAppointments());
        assertEquals(expectedFamilyAffiliations, model.getFamilyAffiliations());
        assertEquals(expectedFamilyFinancialInterests, model.getFamilyFinancialInterests());
        assertEquals(expectedFinancialInterests, model.getFinancialInterests());
        assertEquals(expectedPrincipalEmployer, model.getPrincipalEmployer());
        assertEquals(expectedProfessionalAffiliations, model.getProfessionalAffiliations());
        assertEquals(expectedRole, model.getRole());
        assertEquals(fixedClock, model.getEdiReviewDate());
        assertEquals(EDIStatus.INCOMPLETE, model.getEdiStatus());
        assertTrue( model.isEdiUpdateEnabled());



    }


    private void setupAffiliations() {


        expectedAddress = newAddressResource()
                .withAddressLine1("1 Testing Lane")
                .withTown("Testville")
                .withCounty("South Testshire")
                .withPostcode("TES TEST")
                .build();

        expectedInnovationAreas = newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);

        profile = newProfileResource()
                .withSkillsAreas("A Skill")
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(expectedInnovationAreas)
                .withAddress(expectedAddress)
                .build();
        expectedProfile = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

         expectedDetailsViewModel = new AssessorProfileDetailsViewModel(user, profile);

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
