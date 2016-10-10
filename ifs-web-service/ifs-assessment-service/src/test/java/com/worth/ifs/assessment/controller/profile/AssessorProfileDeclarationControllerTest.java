package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.form.profile.AssessorProfileAppointmentForm;
import com.worth.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import com.worth.ifs.assessment.form.profile.AssessorProfileFamilyAffiliationForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import com.worth.ifs.assessment.viewmodel.profile.AssessorProfileDeclarationViewModel;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.worth.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.AffiliationType.*;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorProfileDeclarationControllerTest extends BaseControllerMockMVCTest<AssessorProfileDeclarationController> {
    @Spy
    @InjectMocks
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Override
    protected AssessorProfileDeclarationController supplyControllerUnderTest() {
        return new AssessorProfileDeclarationController();
    }

    @Test
    public void getDeclaration() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        int year = 2016;
        setClockToStartOfDay(LocalDate.of(year, JANUARY, 1));
        LocalDate expectedDeclarationDate = getFinancialYearEndDate(year);

        String expectedPrincipalEmployer = "Big Name Corporation";
        String expectedRole = "Financial Accountant";
        String expectedProfessionalAffiliations = "Professional Affiliations...";

        Boolean expectedHasAppointments = TRUE;
        List<AssessorProfileAppointmentForm> expectedAppointments = asList(
                new AssessorProfileAppointmentForm("Org 1", "Pos 1"),
                new AssessorProfileAppointmentForm("Org 2", "Pos 2")
        );

        Boolean expectedHasFinancialInterests = TRUE;
        String expectedFinancialInterests = "Other Financial Interests...";

        Boolean expectedHasFamilyAffiliations = TRUE;
        List<AssessorProfileFamilyAffiliationForm> expectedFamilyAffiliations = asList(
                new AssessorProfileFamilyAffiliationForm("Relation 1", "Org 1", "Pos 1"),
                new AssessorProfileFamilyAffiliationForm("Relation 2", "Org 2", "Pos 2")
        );

        Boolean expectedHasFamilyFinancialInterests = TRUE;
        String expectedFamilyFinancialInterests = "Other Family Financial Interests...";

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

        List<AffiliationResource> appointments = newAffiliationResource()
                .withAffiliationType(PERSONAL)
                .withExists(TRUE)
                .withOrganisation("Org 1", "Org 2")
                .withPosition("Pos 1", "Pos 2")
                .build(2);

        AffiliationResource financialInterests = newAffiliationResource()
                .withAffiliationType(PERSONAL_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFinancialInterests)
                .build();

        List<AffiliationResource> familyAffiliations = newAffiliationResource()
                .withAffiliationType(FAMILY)
                .withExists(TRUE)
                .withRelation("Relation 1", "Relation 2")
                .withOrganisation("Org 1", "Org 2")
                .withPosition("Pos 1", "Pos 2")
                .build(2);

        AffiliationResource familyFinancialInterests = newAffiliationResource()
                .withAffiliationType(FAMILY_FINANCIAL)
                .withExists(TRUE)
                .withDescription(expectedFamilyFinancialInterests)
                .build();

        when(userService.getUserAffiliations(user.getId())).thenReturn(combineLists(
                combineLists(
                        appointments,
                        familyAffiliations
                ),
                principalEmployer,
                professionalAffiliations,
                financialInterests,
                familyFinancialInterests
                )
        );

        AssessorProfileDeclarationViewModel expectedViewModel = new AssessorProfileDeclarationViewModel(expectedDeclarationDate);
        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();
        expectedForm.setPrincipalEmployer(expectedPrincipalEmployer);
        expectedForm.setRole(expectedRole);
        expectedForm.setProfessionalAffiliations(expectedProfessionalAffiliations);
        expectedForm.setHasAppointments(expectedHasAppointments);
        expectedForm.setAppointments(expectedAppointments);
        expectedForm.setHasFinancialInterests(expectedHasFinancialInterests);
        expectedForm.setFinancialInterests(expectedFinancialInterests);
        expectedForm.setHasFamilyAffiliations(expectedHasFamilyAffiliations);
        expectedForm.setFamilyAffiliations(expectedFamilyAffiliations);
        expectedForm.setHasFamilyFinancialInterests(expectedHasFamilyFinancialInterests);
        expectedForm.setFamilyFinancialInterests(expectedFamilyFinancialInterests);


        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(view().name("profile/declaration-of-interest"));

        verify(userService).getUserAffiliations(user.getId());
    }


    @Test
    public void getDeclaration_noAffiliations() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        // The form should have no fields populated
        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();

        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void getDeclaration_noAppointments() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        List<AffiliationResource> appointments = newAffiliationResource()
                .withAffiliationType(PERSONAL)
                .withExists(FALSE)
                .build(1);

        when(userService.getUserAffiliations(user.getId())).thenReturn(appointments);

        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();
        expectedForm.setHasAppointments(FALSE);

        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void getDeclaration_noFinancialInterests() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        List<AffiliationResource> financialInterests = newAffiliationResource()
                .withAffiliationType(PERSONAL_FINANCIAL)
                .withExists(FALSE)
                .build(1);

        when(userService.getUserAffiliations(user.getId())).thenReturn(financialInterests);

        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();
        expectedForm.setHasFinancialInterests(FALSE);

        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void getDeclaration_noFamilyInterests() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        List<AffiliationResource> appointments = newAffiliationResource()
                .withAffiliationType(FAMILY)
                .withExists(FALSE)
                .build(1);

        when(userService.getUserAffiliations(user.getId())).thenReturn(appointments);

        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();
        expectedForm.setHasFamilyAffiliations(FALSE);

        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void getDeclaration_noFamilyFinancialInterests() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        List<AffiliationResource> familyFinancialInterests = newAffiliationResource()
                .withAffiliationType(FAMILY_FINANCIAL)
                .withExists(FALSE)
                .build(1);

        when(userService.getUserAffiliations(user.getId())).thenReturn(familyFinancialInterests);

        AssessorProfileDeclarationForm expectedForm = new AssessorProfileDeclarationForm();
        expectedForm.setHasFamilyFinancialInterests(FALSE);

        mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm));
    }

    @Test
    public void getDeclaration_beforeFinancialYearEndInCurrentYear() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        int year = 2016;
        // Set the clock such that the financial year end has yet to be reached this year
        setClockToStartOfDay(getFinancialYearEndDate(year).minusDays(1));
        // Expect the declaration date to be the financial year end of the current year
        LocalDate expectedDeclarationDate = getFinancialYearEndDate(year);

        MvcResult result = mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andReturn();

        AssessorProfileDeclarationViewModel viewModel = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expectedDeclarationDate, viewModel.getDeclarationDate());
    }

    @Test
    public void getDeclaration_onFinancialYearEndInCurrentYear() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        int year = 2016;
        // Set the clock such that the financial year end has been reached this year
        setClockToStartOfDay(getFinancialYearEndDate(year));
        // Expect the declaration date to be the financial year end of the next year
        LocalDate expectedDeclarationDate = getFinancialYearEndDate(year).plusYears(1);

        MvcResult result = mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andReturn();

        AssessorProfileDeclarationViewModel viewModel = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expectedDeclarationDate, viewModel.getDeclarationDate());
    }

    @Test
    public void getDeclaration_afterFinancialYearEndInCurrentYear() throws Exception {
        UserResource user = newUserResource().build();
        setLoggedInUser(user);

        int year = 2016;
        // Set the clock such that the financial year end has been passed this year
        setClockToStartOfDay(getFinancialYearEndDate(year).plusDays(1));
        // Expect the declaration date to be the financial year end of the next year
        LocalDate expectedDeclarationDate = getFinancialYearEndDate(year).plusYears(1);

        MvcResult result = mockMvc.perform(get("/profile/declaration"))
                .andExpect(status().isOk())
                .andReturn();

        AssessorProfileDeclarationViewModel viewModel = (AssessorProfileDeclarationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(expectedDeclarationDate, viewModel.getDeclarationDate());
    }

    @Test
    public void submitDeclaration() throws Exception {

    }

    @Test
    public void addAppointment() throws Exception {

    }

    @Test
    public void removeAppointment() throws Exception {

    }

    @Test
    public void addFamilyMemberAffiliation() throws Exception {

    }

    @Test
    public void removeFamilyMemberAffiliation() throws Exception {

    }

    private LocalDate getFinancialYearEndDate(int year) {
        return LocalDate.of(year, MARCH, 30);
    }

    private void setClockToStartOfDay(LocalDate date) {
        Clock clock = Clock.fixed(date.atStartOfDay(systemDefault()).toInstant(), systemDefault());
        AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator = (AssessorProfileDeclarationModelPopulator) ReflectionTestUtils.getField(controller, AssessorProfileDeclarationController.class, "assessorProfileDeclarationModelPopulator");
        ReflectionTestUtils.setField(assessorProfileDeclarationModelPopulator, "clock", clock, Clock.class);
    }
}