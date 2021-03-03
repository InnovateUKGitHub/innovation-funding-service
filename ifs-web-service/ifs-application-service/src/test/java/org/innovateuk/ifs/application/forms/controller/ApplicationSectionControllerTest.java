package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.ApplicationUrlHelper;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.PROJECT_LOCATION;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class ApplicationSectionControllerTest extends BaseControllerMockMVCTest<ApplicationSectionController> {
    private static final long APPLICATION_ID = 1L;
    private static final long COMPETITION_ID = 2L;
    private static final long SECTION_ID = 3L;
    private static final long ORGANISATION_ID = 4L;
    private static final SectionType SECTION_TYPE = PROJECT_LOCATION;
    private static final ApplicationResource APPLICATION = newApplicationResource().withId(APPLICATION_ID).withCompetition(COMPETITION_ID).build();
    private static final SectionResource SECTION = newSectionResource().withId(SECTION_ID).withCompetition(COMPETITION_ID).withType(SECTION_TYPE).build();
    private static final ProcessRoleResource PROCESS_ROLE = newProcessRoleResource().withApplication(APPLICATION_ID).withOrganisation(ORGANISATION_ID).build();

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private ApplicationUrlHelper applicationUrlHelper;

    @Mock
    private SectionRestService sectionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected ApplicationSectionController supplyControllerUnderTest() {
        return new ApplicationSectionController();
    }

    @Test
    public void redirectToSectionManagement() throws Exception {
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(APPLICATION));
        when(sectionRestService.getSectionsByCompetitionIdAndType(COMPETITION_ID, SECTION_TYPE)).thenReturn(restSuccess(singletonList(SECTION)));
        when(applicationUrlHelper.getSectionUrl(SECTION_TYPE, SECTION_ID, APPLICATION_ID, ORGANISATION_ID, COMPETITION_ID)).thenReturn(Optional.of("redirectUrl"));

        mockMvc.perform(get("/application/{applicationId}/form/{sectionType}/{organisationId}", APPLICATION_ID, SECTION_TYPE.name(), ORGANISATION_ID))
                .andExpect(redirectedUrl("redirectUrl"));
    }

    @Test
    public void redirectToSection() throws Exception {
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(APPLICATION));
        when(sectionRestService.getSectionsByCompetitionIdAndType(COMPETITION_ID, SECTION_TYPE)).thenReturn(restSuccess(singletonList(SECTION)));
        when(processRoleRestService.findProcessRole(loggedInUser.getId(), APPLICATION_ID)).thenReturn(restSuccess(PROCESS_ROLE));
        when(applicationUrlHelper.getSectionUrl(SECTION_TYPE, SECTION_ID, APPLICATION_ID, ORGANISATION_ID, COMPETITION_ID)).thenReturn(Optional.of("redirectUrl"));

        mockMvc.perform(get("/application/{applicationId}/form/{sectionType}", APPLICATION_ID, PROJECT_LOCATION.name()))
                .andExpect(redirectedUrl("redirectUrl"));
    }

    @Test
    public void getSectionApplicant() throws Exception {
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(APPLICATION));
        when(sectionRestService.getById(SECTION_ID)).thenReturn(restSuccess(SECTION));
        when(processRoleRestService.findProcessRole(loggedInUser.getId(), APPLICATION_ID)).thenReturn(restSuccess(PROCESS_ROLE));
        when(applicationUrlHelper.getSectionUrl(SECTION_TYPE, SECTION_ID, APPLICATION_ID, ORGANISATION_ID, COMPETITION_ID)).thenReturn(Optional.of("redirectUrl"));

        mockMvc.perform(get("/application/{applicationId}/form/section/{sectionId}", APPLICATION_ID, SECTION_ID))
                .andExpect(redirectedUrl("redirectUrl"));
    }

    @Test
    public void getSectionInternalUser() throws Exception {
        when(applicationRestService.getApplicationById(APPLICATION_ID)).thenReturn(restSuccess(APPLICATION));
        when(sectionRestService.getById(SECTION_ID)).thenReturn(restSuccess(SECTION));
        when(processRoleRestService.findProcessRole(loggedInUser.getId(), APPLICATION_ID)).thenReturn(restSuccess(PROCESS_ROLE));
        when(applicationUrlHelper.getSectionUrl(SECTION_TYPE, SECTION_ID, APPLICATION_ID, ORGANISATION_ID, COMPETITION_ID)).thenReturn(Optional.of("redirectUrl"));

        mockMvc.perform(get("/application/{applicationId}/form/section/{sectionId}/{organisationId}", APPLICATION_ID, SECTION_ID, ORGANISATION_ID))
                .andExpect(redirectedUrl("redirectUrl"));
    }
}