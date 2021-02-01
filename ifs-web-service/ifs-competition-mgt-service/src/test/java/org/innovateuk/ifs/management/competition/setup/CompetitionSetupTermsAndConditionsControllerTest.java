package org.innovateuk.ifs.management.competition.setup;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.Validator;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeResourceBuilder.newCompetitionTypeResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionSetupController}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupTermsAndConditionsControllerTest extends BaseControllerMockMVCTest<CompetitionSetupTermsAndConditionsController> {

    private static final Long COMPETITION_ID = 12L;
    private static final String URL_PREFIX = "/competition/setup";

    @Mock
    private CategoryRestService categoryRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private Validator validator;

    @Mock
    private ManageInnovationLeadsModelPopulator manageInnovationLeadsModelPopulator;

    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Override
    protected CompetitionSetupTermsAndConditionsController supplyControllerUnderTest() {
        return new CompetitionSetupTermsAndConditionsController();
    }

    @Before
    public void setUp() {

        when(userRestService.findByUserRole(COMP_ADMIN))
                .thenReturn(
                        restSuccess(newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Admin")
                                .build(1))
                );

        when(userRestService.findByUserRole(INNOVATION_LEAD))
                .thenReturn(
                        restSuccess(newUserResource()
                                .withFirstName("Comp")
                                .withLastName("Technologist")
                                .build(1))
                );

        List<InnovationSectorResource> innovationSectorResources = newInnovationSectorResource()
                .withName("A Innovation Sector")
                .withId(1L)
                .build(1);
        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(innovationSectorResources));

        List<InnovationAreaResource> innovationAreaResources = newInnovationAreaResource()
                .withName("A Innovation Area")
                .withId(2L)
                .withSector(1L)
                .build(1);
        when(categoryRestService.getInnovationAreas()).thenReturn(restSuccess(innovationAreaResources));

        List<CompetitionTypeResource> competitionTypeResources = newCompetitionTypeResource()
                .withId(1L)
                .withName("Programme")
                .withCompetitions(singletonList(COMPETITION_ID))
                .build(1);
        when(competitionRestService.getCompetitionTypes()).thenReturn(restSuccess(competitionTypeResources));

        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(COMPETITION_ID)).thenReturn(true);
    }

    @Test
    public void uploadTermsAndConditions() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(COMPETITION_ID)
                .build();

        String fileName = "termsAndConditionsDoc";
        String originalFileName = "original filename";
        String contentType = "application/json";
        String content = "content";

        MockMultipartFile file = new MockMultipartFile(fileName, originalFileName, contentType, content.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource().build();

        TermsAndConditionsForm form = new TermsAndConditionsForm();
        form.setTermsAndConditionsDoc(file);

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(competitionSetupRestService.uploadCompetitionTerms(COMPETITION_ID, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file))).thenReturn(restSuccess(fileEntryResource));

        mockMvc.perform(multipart(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID))
                .file(file)
                .param("uploadTermsAndConditionsDoc", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID)));

        InOrder inOrder = inOrder(competitionRestService, competitionSetupRestService, competitionSetupService);
        inOrder.verify(competitionRestService).getCompetitionById(COMPETITION_ID);
        inOrder.verify(competitionSetupRestService)
                .uploadCompetitionTerms(COMPETITION_ID, file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file));
        inOrder.verify(competitionSetupService)
                .saveCompetitionSetupSection(form, competitionResource, CompetitionSetupSection.TERMS_AND_CONDITIONS);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitTermsAndConditionsSectionDetails() throws Exception {
        GrantTermsAndConditionsResource nonProcurementTerms = newGrantTermsAndConditionsResource()
                .withName("Non procurement terms")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competition));
        when(termsAndConditionsRestService.getById(nonProcurementTerms.getId())).thenReturn(restSuccess(nonProcurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competition),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(nonProcurementTerms.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions"));

        InOrder inOrder = inOrder(competitionSetupService, competitionSetupRestService, competitionRestService, termsAndConditionsRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competition.getId());
        inOrder.verify(termsAndConditionsRestService).getById(nonProcurementTerms.getId());
        inOrder.verify(competitionSetupRestService).deleteCompetitionTerms(competition.getId());
        inOrder.verify(competitionSetupService).saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competition),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void submitTermsAndConditionsSectionDetails_procurement() throws Exception {
        GrantTermsAndConditionsResource procurementTerms = newGrantTermsAndConditionsResource().withName("Procurement").build();
        CompetitionResource competitionWithTermsDoc = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithTermsDoc));
        when(termsAndConditionsRestService.getById(procurementTerms.getId())).thenReturn(restSuccess(procurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(procurementTerms.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions"));

        verify(competitionSetupService).saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS));
    }

    @Test
    public void submitTermsAndConditionsSectionDetails_procurementNoFileUploaded() throws Exception {
        GrantTermsAndConditionsResource procurementTerms = newGrantTermsAndConditionsResource().withName("Procurement").build();
        CompetitionResource competitionWithoutTermsDoc = newCompetitionResource().withId(COMPETITION_ID).build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithoutTermsDoc));
        when(termsAndConditionsRestService.getById(procurementTerms.getId())).thenReturn(restSuccess(procurementTerms));
        when(competitionSetupService.saveCompetitionSetupSection(
                any(TermsAndConditionsForm.class),
                eq(competitionWithoutTermsDoc),
                eq(CompetitionSetupSection.TERMS_AND_CONDITIONS))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL_PREFIX + "/" + COMPETITION_ID + "/section/terms-and-conditions")
                .param("termsAndConditionsId", String.valueOf(procurementTerms.getId())))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"))
                .andExpect(model().attributeHasFieldErrors("competitionSetupForm", "termsAndConditionsDoc"));
    }

    @Test
    public void deleteTermsAndConditions() throws Exception {
        CompetitionResource competitionWithTermsDoc = newCompetitionResource()
                .withId(COMPETITION_ID)
                .withCompetitionTerms(newFileEntryResource().build())
                .build();

        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionWithTermsDoc));
        when(competitionSetupRestService.deleteCompetitionTerms(COMPETITION_ID)).thenReturn(restSuccess());

        mockMvc.perform(multipart(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID))
                .param("deleteTermsAndConditionsDoc", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("%s/%d/section/terms-and-conditions", URL_PREFIX, COMPETITION_ID)));

        verify(competitionRestService).getCompetitionById(COMPETITION_ID);
        verify(competitionSetupRestService).deleteCompetitionTerms(COMPETITION_ID);
    }
}