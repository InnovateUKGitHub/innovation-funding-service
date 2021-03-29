package org.innovateuk.ifs.project.grantofferletter.populator;

import org.apache.commons.collections.ListUtils;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.AcademicFinanceTableModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.IndustrialFinanceTableModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.SummaryFinanceTableModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.finance.builder.NoteResourceBuilder.newNoteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterTemplatePopulatorTest {

    @InjectMocks
    private GrantOfferLetterTemplatePopulator populator;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceNotesRestService projectFinanceNotesRestService;

    @Mock
    private IndustrialFinanceTableModelPopulator industrialFinanceTableModelPopulator;

    @Mock
    private AcademicFinanceTableModelPopulator academicFinanceTableModelPopulator;

    @Mock
    private SummaryFinanceTableModelPopulator summaryFinanceTableModelPopulator;

    private static final long APPLICATION_ID = 123L;

    @Test
    public void populate() {

        GrantTermsAndConditionsResource tsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Terms and conditions template")
                        .build();

        CompetitionResource competition =
                newCompetitionResource()
                        .withName("Competition name")
                        .withTermsAndConditions(tsAndCs)
                        .build();

        AddressResource address = address();
        ProjectResource project = project(competition, address);
        OrganisationResource leadOrg = leadOrg();
        UserResource projectManager = projectManager();
        ProjectUserResource projectManagerProjectUser = projectManagerProjectUser(leadOrg, projectManager);

        List<ProjectFinanceResource> projectFinances =
                newProjectFinanceResource()
                        .withProject(project.getId())
                        .build(1);

        List<NoteResource> notes = newNoteResource().build(2);
        Map<String, ProjectFinanceResource> finances = asMap(leadOrg.getName(), projectFinances);
        IndustrialFinanceTableModel industrialFinanceTable = industrialFinanceTable(leadOrg, finances);
        AcademicFinanceTableModel academicFinanceTable = academicFinanceTable(leadOrg, finances);
        SummaryFinanceTableModel summaryFinanceTable = summaryFinanceTable();

        when(organisationRestService.getOrganisationById(leadOrg.getId())).thenReturn(restSuccess(leadOrg));
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(projectManagerProjectUser));
        when(userRestService.retrieveUserById(projectManager.getId())).thenReturn(restSuccess(projectManager));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(projectFinances));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(0).getId())).thenReturn(restSuccess(notes));

        when(industrialFinanceTableModelPopulator.createTable(any(), any())).thenReturn(industrialFinanceTable);
        when(academicFinanceTableModelPopulator.createTable(any(), any())).thenReturn(academicFinanceTable);
        when(summaryFinanceTableModelPopulator.createTable(any(), any())).thenReturn(summaryFinanceTable);

        GrantOfferLetterTemplateViewModel model = populator.populate(project, competition);

        assertEquals(APPLICATION_ID, model.getApplicationId());
        assertEquals(projectManager.getFirstName(), model.getProjectManagerFirstName());
        assertEquals(projectManager.getLastName(), model.getProjectManagerLastName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(address.getAddressLine1(), model.getProjectAddress().get(0));
        assertEquals(address.getAddressLine2(), model.getProjectAddress().get(1));
        assertEquals(leadOrg.getName(), model.getLeadOrgName());
        assertEquals(notes, model.getNotes());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isSingleTermsAndConditionsTemplatePresent());
        assertEquals(tsAndCs.getTemplate(), model.getSingleTermsAndConditionsTemplate());
        assertEquals(industrialFinanceTable, model.getIndustrialFinanceTable());
        assertEquals(academicFinanceTable, model.getAcademicFinanceTable());
        assertEquals(summaryFinanceTable, model.getSummaryFinanceTable());
    }

    @Test
    public void populateWithDualTermsAndConditions() {

        GrantTermsAndConditionsResource tsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Terms and conditions template")
                        .build();
        GrantTermsAndConditionsResource otherTsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Other terms and conditions template")
                        .build();

        CompetitionResource competition =
                newCompetitionResource()
                        .withName("Competition name")
                        .withTermsAndConditions(tsAndCs)
                        .withOtherFundingRulesTermsAndConditions(otherTsAndCs)
                        .build();

        AddressResource address = address();
        ProjectResource project = project(competition, address);
        OrganisationResource leadOrg = leadOrg();
        UserResource projectManager = projectManager();
        ProjectUserResource projectManagerProjectUser = projectManagerProjectUser(leadOrg, projectManager);

        List<ProjectFinanceResource> projectFinances =
                newProjectFinanceResource()
                        .withProject(project.getId())
                        .withNorthernIrelandDeclaration(false, true)
                        .build(2);

        List<NoteResource> notes = newNoteResource().build(2);
        List<NoteResource> notes2 = newNoteResource().build(2);
        Map<String, ProjectFinanceResource> finances = asMap(leadOrg.getName(), projectFinances);
        IndustrialFinanceTableModel industrialFinanceTable = industrialFinanceTable(leadOrg, finances);
        AcademicFinanceTableModel academicFinanceTable = academicFinanceTable(leadOrg, finances);
        SummaryFinanceTableModel summaryFinanceTable = summaryFinanceTable();

        when(organisationRestService.getOrganisationById(leadOrg.getId())).thenReturn(restSuccess(leadOrg));
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(projectManagerProjectUser));
        when(userRestService.retrieveUserById(projectManager.getId())).thenReturn(restSuccess(projectManager));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(projectFinances));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(0).getId())).thenReturn(restSuccess(notes));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(1).getId())).thenReturn(restSuccess(notes2));

        when(industrialFinanceTableModelPopulator.createTable(any(), any())).thenReturn(industrialFinanceTable);
        when(academicFinanceTableModelPopulator.createTable(any(), any())).thenReturn(academicFinanceTable);
        when(summaryFinanceTableModelPopulator.createTable(any(), any())).thenReturn(summaryFinanceTable);

        GrantOfferLetterTemplateViewModel model = populator.populate(project, competition);

        assertEquals(APPLICATION_ID, model.getApplicationId());
        assertEquals(projectManager.getFirstName(), model.getProjectManagerFirstName());
        assertEquals(projectManager.getLastName(), model.getProjectManagerLastName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(address.getAddressLine1(), model.getProjectAddress().get(0));
        assertEquals(address.getAddressLine2(), model.getProjectAddress().get(1));
        assertEquals(leadOrg.getName(), model.getLeadOrgName());
        assertEquals(ListUtils.union(notes, notes2), model.getNotes());
        assertEquals(project.getName(), model.getProjectName());
        assertFalse(model.isSingleTermsAndConditionsTemplatePresent());
        assertEquals(2, model.getTermsAndConditionsTemplates().size());
        assertEquals(tsAndCs.getTemplate(), model.getTermsAndConditionsTemplates().get("state aid"));
        assertEquals(otherTsAndCs.getTemplate(), model.getTermsAndConditionsTemplates().get("subsidy control"));
        assertEquals(industrialFinanceTable, model.getIndustrialFinanceTable());
        assertEquals(academicFinanceTable, model.getAcademicFinanceTable());
        assertEquals(summaryFinanceTable, model.getSummaryFinanceTable());
    }

    @Test
    public void populateWithOnlyOtherTermsAndConditions() {

        GrantTermsAndConditionsResource tsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Terms and conditions template")
                        .build();
        GrantTermsAndConditionsResource otherTsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Other terms and conditions template")
                        .build();

        CompetitionResource competition =
                newCompetitionResource()
                        .withName("Competition name")
                        .withTermsAndConditions(tsAndCs)
                        .withOtherFundingRulesTermsAndConditions(otherTsAndCs)
                        .build();

        AddressResource address = address();
        ProjectResource project = project(competition, address);
        OrganisationResource leadOrg = leadOrg();
        UserResource projectManager = projectManager();
        ProjectUserResource projectManagerProjectUser = projectManagerProjectUser(leadOrg, projectManager);

        List<ProjectFinanceResource> projectFinances =
                newProjectFinanceResource()
                        .withProject(project.getId())
                        .withNorthernIrelandDeclaration(true, true)
                        .build(2);

        List<NoteResource> notes = newNoteResource().build(2);
        List<NoteResource> notes2 = newNoteResource().build(2);

        Map<String, ProjectFinanceResource> finances = asMap(leadOrg.getName(), projectFinances);
        IndustrialFinanceTableModel industrialFinanceTable = industrialFinanceTable(leadOrg, finances);
        AcademicFinanceTableModel academicFinanceTable = academicFinanceTable(leadOrg, finances);
        SummaryFinanceTableModel summaryFinanceTable = summaryFinanceTable();

        when(organisationRestService.getOrganisationById(leadOrg.getId())).thenReturn(restSuccess(leadOrg));
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(projectManagerProjectUser));
        when(userRestService.retrieveUserById(projectManager.getId())).thenReturn(restSuccess(projectManager));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(projectFinances));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(0).getId())).thenReturn(restSuccess(notes));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(1).getId())).thenReturn(restSuccess(notes2));

        when(industrialFinanceTableModelPopulator.createTable(any(), any())).thenReturn(industrialFinanceTable);
        when(academicFinanceTableModelPopulator.createTable(any(), any())).thenReturn(academicFinanceTable);
        when(summaryFinanceTableModelPopulator.createTable(any(), any())).thenReturn(summaryFinanceTable);

        GrantOfferLetterTemplateViewModel model = populator.populate(project, competition);

        assertEquals(APPLICATION_ID, model.getApplicationId());
        assertEquals(projectManager.getFirstName(), model.getProjectManagerFirstName());
        assertEquals(projectManager.getLastName(), model.getProjectManagerLastName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(address.getAddressLine1(), model.getProjectAddress().get(0));
        assertEquals(address.getAddressLine2(), model.getProjectAddress().get(1));
        assertEquals(leadOrg.getName(), model.getLeadOrgName());
        assertEquals(ListUtils.union(notes, notes2), model.getNotes());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isSingleTermsAndConditionsTemplatePresent());
        assertEquals(otherTsAndCs.getTemplate(), model.getSingleTermsAndConditionsTemplate());
        assertEquals(industrialFinanceTable, model.getIndustrialFinanceTable());
        assertEquals(academicFinanceTable, model.getAcademicFinanceTable());
        assertEquals(summaryFinanceTable, model.getSummaryFinanceTable());
    }

    private AcademicFinanceTableModel academicFinanceTable(OrganisationResource leadOrg, Map<String, ProjectFinanceResource> finances) {
        return new AcademicFinanceTableModel(true,
                finances,
                singletonList(leadOrg.getName()),
                BigDecimal.TEN,
                BigDecimal.ONE);
    }

    private ProjectUserResource projectManagerProjectUser(OrganisationResource leadOrg, UserResource projectManager) {
        return newProjectUserResource()
                .withOrganisation(leadOrg.getId())
                .withUser(projectManager.getId())
                .build();
    }

    private OrganisationResource leadOrg() {
        return newOrganisationResource().
                withName("Organisation name")
                .build();
    }

    private AddressResource address() {
        return newAddressResource()
                .withAddressLine1("Address line 1")
                .withAddressLine2("Address line 2")
                .build();
    }

    private SummaryFinanceTableModel summaryFinanceTable() {
        return new SummaryFinanceTableModel(BigDecimal.ONE,
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.ZERO);
    }

    private IndustrialFinanceTableModel industrialFinanceTable(OrganisationResource leadOrg, Map<String, ProjectFinanceResource> finances) {
        return new IndustrialFinanceTableModel(true,
                finances,
                singletonList(leadOrg.getName()),
                BigDecimal.TEN,
                BigDecimal.ONE);
    }

    private UserResource projectManager() {
        return newUserResource()
                .withFirstName("Mr")
                .withLastName("Manager")
                .build();
    }

    private ProjectResource project(CompetitionResource competition, AddressResource address) {
        return newProjectResource()
                .withCompetition(competition.getId())
                .withName("Project name")
                .withApplication(APPLICATION_ID)
                .withAddress(address)
                .build();
    }
}
