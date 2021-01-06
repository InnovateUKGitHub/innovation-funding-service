package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceNotesRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpFinanceModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.KtpGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.threads.resource.NoteResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.finance.builder.NoteResourceBuilder.newNoteResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KtpGrantOfferLetterTemplatePopulatorTest {

    @InjectMocks
    private KtpGrantOfferLetterTemplatePopulator populator;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectFinanceNotesRestService projectFinanceNotesRestService;

    @Mock
    private KtpFinanceModelPopulator ktpFinanceModelPopulator;

    @Test
    public void populate() {
        long leadOrgId = 1L;
        long partnerOrgId = 2L;
        CompetitionResource competition =
                newCompetitionResource()
                        .withName("Competition name")
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
                        .withApplication(123L)
                        .withAddress(address)
                        .build();

        UserResource projectManager =
                newUserResource()
                        .withFirstName("Mr")
                        .withLastName("Manager")
                        .build();

        ProjectUserResource projectManagerProjectUser =
                newProjectUserResource()
                        .withOrganisation(leadOrgId)
                        .withUser(projectManager.getId())
                        .build();

        List<NoteResource> notes = newNoteResource().build(2);

        List<PartnerOrganisationResource> partnerOrganisations = newPartnerOrganisationResource()
                .withOrganisationName("KB", "Partner")
                .withLeadOrganisation(true, false)
                .withOrganisation(leadOrgId, partnerOrgId)
                .build(2);

        List<ProjectFinanceResource> projectFinances =
                newProjectFinanceResource()
                        .withProject(project.getId())
                        .withOrganisation(leadOrgId, partnerOrgId)
                        .build(2);

        KtpFinanceModel ktpFinanceModel = mock(KtpFinanceModel.class);

        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(projectFinances));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(0).getId())).thenReturn(restSuccess(notes));
        when(projectFinanceNotesRestService.findAll(projectFinances.get(1).getId())).thenReturn(restSuccess(new ArrayList<>()));
        when(projectRestService.getProjectManager(project.getId())).thenReturn(restSuccess(projectManagerProjectUser));
        when(userRestService.retrieveUserById(projectManager.getId())).thenReturn(restSuccess(projectManager));
        when(ktpFinanceModelPopulator.populate(project, projectFinances.get(0))).thenReturn(ktpFinanceModel);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).thenReturn(restSuccess(partnerOrganisations));

        KtpGrantOfferLetterTemplateViewModel model = populator.populate(project, competition);

        assertEquals(123L, model.getApplicationId());
        assertEquals(projectManager.getFirstName(), model.getProjectManagerFirstName());
        assertEquals(projectManager.getLastName(), model.getProjectManagerLastName());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(project.getCompetitionName(), model.getCompetitionName());
        assertEquals(address.getAddressLine1(), model.getProjectAddress().get(0));
        assertEquals(address.getAddressLine2(), model.getProjectAddress().get(1));
        assertEquals("KB", model.getLeadOrgName());
        assertEquals("Partner", model.getPartnerOrgName());
        assertEquals(notes, model.getNotes());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(ktpFinanceModel, model.getKtpFinanceModel());
    }
}
