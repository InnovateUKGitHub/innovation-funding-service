package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.finance.view.DefaultFinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceFormHandler;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests {@link ApplicationSectionSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationSectionSaverTest {

    @InjectMocks
    private ApplicationSectionSaver sectionSaver;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private FinanceHandler financeHandler;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private OverheadFileSaver overheadFileSaver;

    @Mock
    private ApplicationSectionFinanceSaver financeSaver;

    @Mock
    private ApplicationQuestionFileSaver fileSaver;

    @Mock
    private ApplicationQuestionNonFileSaver nonFileSaver;

    private final ApplicationResource application = newApplicationResource().withId(1234L).build();
    private final Long competitionId = 23412L;
    private final ApplicationForm form = new ApplicationForm();
    private final Long sectionId = 912509L;
    private final Long userId = 812482L;
    private final Long processRoleId = 2312412L;
    private final Boolean validFinanceTerms = false;

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final SectionResource section = newSectionResource().withId(sectionId).build();


    @Before
    public void setup() {
        when(processRoleService.findProcessRole(userId, application.getId())).thenReturn(newProcessRoleResource().withId(processRoleId).build());
        when(sectionService.getById(sectionId)).thenReturn(section);

        when(organisationService.getOrganisationType(userId, application.getId())).thenReturn(OrganisationTypeEnum.BUSINESS.getId());
        FinanceFormHandler defaultFinanceFormHandler = mock(DefaultFinanceFormHandler.class);
        when(defaultFinanceFormHandler.update(request, userId, application.getId(), competitionId)).thenReturn(new ValidationMessages());
        when(financeHandler.getFinanceFormHandler(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(defaultFinanceFormHandler);
        when(overheadFileSaver.isOverheadFileRequest(request)).thenReturn(false);
    }

    @Test
    public void saveApplicationForm_isFundingRequest_request() {
        Map<String, String[]> params = asMap(REQUESTING_FUNDING, new String[]{});
        when(request.getParameterMap()).thenReturn(params);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(financeSaver, times(1)).handleRequestFundingRequests(params, application.getId(), competitionId, processRoleId);
    }

    @Test
    public void saveApplicationForm_isFundingRequest_notRequesting() {
        Map<String, String[]> params = asMap(NOT_REQUESTING_FUNDING, new String[]{});
        when(request.getParameterMap()).thenReturn(params);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(financeSaver, times(1)).handleRequestFundingRequests(params, application.getId(), competitionId, processRoleId);
    }

    @Test
    public void saveApplicationForm_SaveSection() {
        Map<String, String[]> params = asMap();
        when(request.getParameterMap()).thenReturn(params);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(financeSaver, times(1)).handleMarkAcademicFinancesAsNotRequired(anyLong(), any(SectionResource.class), anyLong(), anyLong(), anyLong());
        verify(organisationService, times(1)).getOrganisationType(userId, application.getId());
        verify(financeSaver, never()).handleRequestFundingRequests(params, application.getId(), competitionId, processRoleId);
    }

    @Test
    public void saveApplicationForm_SaveSection_OverHeadFileRequest() {
        Map<String, String[]> params = asMap();
        when(request.getParameterMap()).thenReturn(params);
        when(overheadFileSaver.isOverheadFileRequest(request)).thenReturn(true);
        when(overheadFileSaver.handleOverheadFileRequest(request)).thenReturn(new ValidationMessages());

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(overheadFileSaver, times(1)).handleOverheadFileRequest(request);
        verify(financeSaver, never()).handleRequestFundingRequests(params, application.getId(), competitionId, processRoleId);
    }

    @Test
    public void saveApplicationForm_SaveSection_OverHeadFileRequest_withErrors() {
        Map<String, String[]> params = asMap();
        when(request.getParameterMap()).thenReturn(params);
        when(overheadFileSaver.isOverheadFileRequest(request)).thenReturn(true);

        ValidationMessages messages = new ValidationMessages();
        messages.addError(new Error("Some error", HttpStatus.BAD_REQUEST));
        when(overheadFileSaver.handleOverheadFileRequest(request)).thenReturn(messages);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertTrue(result.hasErrors());
        verify(overheadFileSaver, times(1)).handleOverheadFileRequest(request);
        verify(financeSaver, never()).handleRequestFundingRequests(params, application.getId(), competitionId, processRoleId);
    }

    @Test
    public void saveApplicationForm_MarkAsComplete() {
        Map<String, String[]> params = asMap(MARK_SECTION_AS_COMPLETE, new String[]{});
        when(request.getParameterMap()).thenReturn(params);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(financeSaver, times(1)).handleStateAid(params, application, form, section);
    }

    @Test
    public void saveApplicationForm_MarkAsIncomplete() {
        Map<String, String[]> params = asMap(MARK_SECTION_AS_INCOMPLETE, new String[]{});
        when(request.getParameterMap()).thenReturn(params);

        ValidationMessages result = sectionSaver.saveApplicationForm(application, competitionId, form, sectionId, userId, request, response, validFinanceTerms);

        assertFalse(result.hasErrors());
        verify(financeSaver, times(1)).handleStateAid(params, application, form, section);
    }
}
