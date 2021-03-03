package org.innovateuk.ifs.project.grantofferletter.populator;

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
import static org.junit.Assert.assertEquals;
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

    @Test
    public void populate() {

        long applicationId = 123L;

        GrantTermsAndConditionsResource tsAndCs =
                newGrantTermsAndConditionsResource()
                        .withTemplate("Terms and conditions template")
                        .build();

        CompetitionResource competition =
                newCompetitionResource()
                        .withName("Competition name")
                        .withTermsAndConditions(tsAndCs)
                        .build();

        AddressResource address =
                newAddressResource()
                        .withAddressLine1("Address line 1")
                        .withAddressLine2("Address line 2")
                        .build();

        ProjectResource project =
                newProjectResource()
                        .withCompetition(competition.getId())
                        .withName("Project name")
                        .withApplication(applicationId)
                        .withAddress(address)
                        .build();

        OrganisationResource leadOrg =
                newOrganisationResource().
                        withName("Organisation name")
                        .build();

        UserResource projectManager =
                newUserResource()
                        .withFirstName("Mr")
                        .withLastName("Manager")
                        .build();

        ProjectUserResource projectManagerProjectUser =
                newProjectUserResource()
                        .withOrganisation(leadOrg.getId())
                        .withUser(projectManager.getId())
                        .build();

        List<ProjectFinanceResource> projectFinances =
                newProjectFinanceResource()
                        .withProject(project.getId())
                        .build(1);

        List<NoteResource> notes = newNoteResource().build(2);

        Map<String, ProjectFinanceResource> finances = asMap(leadOrg.getName(), projectFinances);

        IndustrialFinanceTableModel industrialFinanceTable = new IndustrialFinanceTableModel(true,
                                                                                             finances,
                                                                                             singletonList(leadOrg.getName()),
                                                                                             BigDecimal.TEN,
                                                                                             BigDecimal.ONE);

        AcademicFinanceTableModel academicFinanceTable = new AcademicFinanceTableModel(true,
                                                                                       finances,
                                                                                       singletonList(leadOrg.getName()),
                                                                                       BigDecimal.TEN,
                                                                                       BigDecimal.ONE);

        SummaryFinanceTableModel summaryFinanceTable = new SummaryFinanceTableModel(BigDecimal.ONE,
                                                                                    BigDecimal.TEN,
                                                                                    BigDecimal.ONE,
                                                                                    BigDecimal.ZERO);

        when(organisationRestService.getOrganisationById(leadOrg.getId())).thenReturn(restSuccess(leadOrg));
        when(projectService.getProjectManager(project.getId())).thenReturn(Optional.of(projectManagerProjectUser));
        when(userRestService.retrieveUserById(projectManager.getId())).thenReturn(restSuccess(projectManager));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(projectFinances));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(0).getId())).thenReturn(restSuccess(notes));

        when(industrialFinanceTableModelPopulator.createTable(any(), any())).thenReturn(industrialFinanceTable);
        when(academicFinanceTableModelPopulator.createTable(any(), any())).thenReturn(academicFinanceTable);
        when(summaryFinanceTableModelPopulator.createTable(any(), any())).thenReturn(summaryFinanceTable);

        GrantOfferLetterTemplateViewModel model = populator.populate(project, competition);

        assertEquals(applicationId, model.getApplicationId());
        assertEquals(projectManager.getFirstName(), model.getProjectManagerFirstName());
        assertEquals(projectManager.getLastName(), model.getProjectManagerLastName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(address.getAddressLine1(), model.getProjectAddress().get(0));
        assertEquals(address.getAddressLine2(), model.getProjectAddress().get(1));
        assertEquals(leadOrg.getName(), model.getLeadOrgName());
        assertEquals(notes, model.getNotes());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(tsAndCs.getTemplate(), model.getTermsAndConditionsTemplate());
        assertEquals(industrialFinanceTable, model.getIndustrialFinanceTable());
        assertEquals(academicFinanceTable, model.getAcademicFinanceTable());
        assertEquals(summaryFinanceTable, model.getSummaryFinanceTable());
    }
}
