package org.innovateuk.ifs.populator;

import org.innovateuk.ifs.BaseUnitTest;
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
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.AffiliationType.FAMILY_FINANCIAL;
import static org.innovateuk.ifs.user.resource.AffiliationType.PERSONAL_FINANCIAL;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorProfileDeclarationModelPopulatorTest extends BaseUnitTest{

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

        setupAffiliations();

        when(assessorRestService.getAssessorProfile(user.getId())).thenReturn(restSuccess(expectedProfile));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(expectedDetailsViewModel);

        AssessorProfileDeclarationViewModel model = populator.populateModel(user, profile, Optional.of(competition.getId()), "", false);
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
