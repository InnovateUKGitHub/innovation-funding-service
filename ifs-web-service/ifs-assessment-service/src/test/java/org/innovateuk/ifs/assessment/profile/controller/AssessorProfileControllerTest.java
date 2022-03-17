package org.innovateuk.ifs.assessment.profile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.affiliation.service.AffiliationRestService;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.populator.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileDetailsModelPopulator;
import org.innovateuk.ifs.populator.AssessorProfileSkillsModelPopulator;
import org.innovateuk.ifs.profile.service.ProfileRestService;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.viewmodel.AssessorProfileDeclarationViewModel;
import org.innovateuk.ifs.viewmodel.AssessorProfileDetailsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.AffiliationListResourceBuilder.newAffiliationListResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessorProfileControllerTest extends BaseControllerMockMVCTest<AssessorProfileController> {

    @Spy
    @InjectMocks
    private AssessorProfileSkillsModelPopulator assessorProfileSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Mock
    private AssessorProfileDetailsModelPopulator assessorProfileDetailsModelPopulator;

    @Mock
    private ProfileRestService profileRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Mock
    private AffiliationRestService affiliationRestService;

    @Override
    protected AssessorProfileController supplyControllerUnderTest() {
        return new AssessorProfileController(
                assessorProfileSkillsModelPopulator,
                assessorProfileDeclarationModelPopulator,
                profileRestService,
                assessorRestService
                );
    }

    @Test
    public void getDeclaration() throws Exception {
        UserResource user = newUserResource().build();
        BusinessType businessType = BUSINESS;

        ProfileResource profile = newProfileResource()
                .withBusinessType(businessType)
                .build();
        setLoggedInUser(user);

        String expectedPrincipalEmployer = "Big Name Corporation";
        String expectedRole = "Financial Accountant";
        String expectedProfessionalAffiliations = "Professional affiliations...";
        String expectedFinancialInterests = "Other financial interests...";
        String expectedFamilyFinancialInterests = "Other family financial interests...";

        List<AffiliationResource> expectedAppointments = newAffiliationResource()
                .withAffiliationType(PERSONAL)
                .withOrganisation("Org 1", "Org 2")
                .withPosition("Pos 1", "Post 2")
                .withExists(TRUE)
                .build(2);
        List<AffiliationResource> expectedFamilyAffiliations = newAffiliationResource()
                .withAffiliationType(FAMILY)
                .withRelation("Relation 1", "Relation 2")
                .withOrganisation("Org 1", "Org 2")
                .withExists(TRUE)
                .build(2);
        AffiliationResource principalEmployer = newAffiliationResource()
                .withAffiliationType(EMPLOYER)
                .withExists(TRUE)
                .withOrganisation(expectedPrincipalEmployer)
                .withPosition(expectedRole)
                .build();
        AffiliationResource professionalAffiliations = newAffiliationResource()
                .withAffiliationType(PROFESSIONAL)
                .withExists(TRUE)
                .withDescription(expectedProfessionalAffiliations)
                .build();
        AffiliationResource financialInterests = newAffiliationResource()
                .withAffiliationType(PERSONAL_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFinancialInterests)
                .build();
        AffiliationResource familyFinancialInterests = newAffiliationResource()
                .withAffiliationType(FAMILY_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFamilyFinancialInterests)
                .build();

        when(affiliationRestService.getUserAffiliations(user.getId()))
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

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = new AssessorProfileDetailsViewModel(user, profile);

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

        when(assessorRestService.getAssessorProfile(anyLong())).thenReturn(restSuccess(assessorProfileResource));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(assessorProfileDetailsViewModel);

        MvcResult result = mockMvc.perform(get("/profile/details/declaration"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/declaration-of-interest"))
                .andReturn();

        AssessorProfileDeclarationViewModel model = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(expectedAppointments, model.getAppointments());
        assertEquals(expectedFamilyAffiliations, model.getFamilyAffiliations());
        assertEquals(expectedFamilyFinancialInterests, model.getFamilyFinancialInterests());
        assertEquals(expectedFinancialInterests, model.getFinancialInterests());
        assertEquals(expectedPrincipalEmployer, model.getPrincipalEmployer());
        assertEquals(expectedProfessionalAffiliations, model.getProfessionalAffiliations());
        assertEquals(expectedRole, model.getRole());

        verify(affiliationRestService).getUserAffiliations(user.getId());
    }

    @Test
    public void getDeclaration_notCompleted() throws Exception {
        UserResource user = newUserResource().build();
        BusinessType businessType = BUSINESS;

        ProfileResource profile = newProfileResource()
                .withBusinessType(businessType)
                .build();
        setLoggedInUser(user);

        AssessorProfileDetailsViewModel assessorProfileDetailsViewModel = new AssessorProfileDetailsViewModel(user, profile);

        AffiliationListResource affiliationListResource = newAffiliationListResource()
                .withAffiliationList(emptyList())
                .build();

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(user)
                .withProfile(profile)
                .build();

        when(affiliationRestService.getUserAffiliations(user.getId())).thenReturn(restSuccess(affiliationListResource));
        when(assessorRestService.getAssessorProfile(anyLong())).thenReturn(restSuccess(assessorProfileResource));
        when(assessorProfileDetailsModelPopulator.populateModel(user, profile)).thenReturn(assessorProfileDetailsViewModel);

        MvcResult result = mockMvc.perform(get("/profile/details/declaration"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/declaration-of-interest"))
                .andReturn();

        AssessorProfileDeclarationViewModel model = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(emptyList(), model.getAppointments());
        assertEquals(emptyList(), model.getFamilyAffiliations());
        assertEquals(null, model.getFamilyFinancialInterests());
        assertEquals(null, model.getFinancialInterests());
        assertEquals(null, model.getPrincipalEmployer());
        assertEquals(null, model.getProfessionalAffiliations());
        assertEquals(null, model.getRole());

        verify(affiliationRestService).getUserAffiliations(user.getId());
    }

    private List<InnovationAreaResource> setUpInnovationAreasForSector(String sectorName, String... innovationAreaNames) {
        return newInnovationAreaResource()
                .withSectorName(sectorName)
                .withName(innovationAreaNames)
                .build(innovationAreaNames.length);
    }

    private UserResource setUpProfileSkills(BusinessType businessType, String skillAreas, List<InnovationAreaResource> innovationAreaResources) {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        when(profileRestService.getProfileSkills(user.getId())).thenReturn(restSuccess(newProfileSkillsResource()
                .withUser(user.getId())
                .withInnovationAreas(innovationAreaResources)
                .withBusinessType(businessType)
                .withSkillsAreas(skillAreas)
                .build()));

        return user;
    }
}
