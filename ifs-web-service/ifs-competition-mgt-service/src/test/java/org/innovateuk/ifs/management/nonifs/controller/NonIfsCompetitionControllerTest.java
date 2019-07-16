package org.innovateuk.ifs.management.nonifs.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm;
import org.innovateuk.ifs.management.nonifs.formpopulator.NonIfsDetailsFormPopulator;
import org.innovateuk.ifs.management.nonifs.modelpopulator.NonIfsDetailsViewModelPopulator;
import org.innovateuk.ifs.management.nonifs.saver.NonIfsDetailsFormSaver;
import org.innovateuk.ifs.management.nonifs.viewmodel.NonIfsDetailsViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.validation.BindingResultUtils.getBindingResult;

public class NonIfsCompetitionControllerTest extends BaseControllerMockMVCTest<NonIfsCompetitionController> {

    @Mock
    public NonIfsDetailsFormPopulator nonIfsDetailsFormPopulator;
    @Mock
    public NonIfsDetailsViewModelPopulator nonIfsDetailsViewModelPopulator;
    @Mock
    public NonIfsDetailsFormSaver nonIfsDetailsFormSaver;
    @Mock
    public CompetitionSetupRestService competitionSetupRestService;
    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected NonIfsCompetitionController supplyControllerUnderTest() {
        return new NonIfsCompetitionController();
    }

    @Test
    public void testCreate() throws Exception {
        Long competitionId = 10L;

        when(competitionSetupRestService.createNonIfs()).thenReturn(restSuccess(newCompetitionResource().withId(competitionId).build()));
        mockMvc.perform(get("/non-ifs-competition/create/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/non-ifs-competition/setup/"+competitionId));
    }

    @Test
    public void testDetails() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        NonIfsDetailsForm nonIfsDetailsForm = new NonIfsDetailsForm();
        NonIfsDetailsViewModel nonIfsDetailsViewModel = mock(NonIfsDetailsViewModel.class);

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormPopulator.populate(competitionResource)).thenReturn(nonIfsDetailsForm);
        when(nonIfsDetailsViewModelPopulator.populate(competitionResource)).thenReturn(nonIfsDetailsViewModel);

        mockMvc.perform(get("/non-ifs-competition/setup/"+competitionId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("competition/non-ifs-details"))
                .andExpect(model().attribute("model", nonIfsDetailsViewModel))
                .andExpect(model().attribute("form", nonIfsDetailsForm));
    }

    @Test
    public void testDetails_whenCompetitionIsIfsRedirectionShouldOccur() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(false).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/non-ifs-competition/setup/"+competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/"+competitionId));
    }

    @Test
    public void testSave() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormSaver.save(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/non-ifs-competition/setup/"+competitionId)
                .param("title", "Competition Title")
                .param("fundingType", FundingType.GRANT.name())
                .param("innovationSectorCategoryId", "12")
                .param("innovationAreaCategoryId", "13")
                .param("openDate.year", "2017")
                .param("openDate.month", "01")
                .param("openDate.day", "01")
                .param("closeDate.year", "2017")
                .param("closeDate.month", "01")
                .param("closeDate.day", "01")
                .param("closeDate.time", "NINE_AM")
                .param("registrationCloseDate.year", "2017")
                .param("registrationCloseDate.month", "01")
                .param("registrationCloseDate.day", "01")
                .param("url","https://worth.systems"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/public-content/" + competitionId));

        verify(nonIfsDetailsFormSaver, times(1)).save(any(), any());
    }

    @Test
    public void testSave_emptyInputsShouldReturnAppropriateErrors() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormSaver.save(any(), any())).thenReturn(serviceSuccess());

        MvcResult mvcResult = mockMvc.perform(post("/non-ifs-competition/setup/"+competitionId)
                .param("title", "")
                .param("innovationSectorCategoryId", "")
                .param("innovationAreaCategoryId", "")
                .param("openDate.year", "")
                .param("openDate.month", "")
                .param("openDate.day", "")
                .param("closeDate.year", "")
                .param("closeDate.month", "")
                .param("closeDate.day", "")
                .param("closeDate.time", "")
                .param("registrationCloseDate.year", "")
                .param("registrationCloseDate.month", "")
                .param("registrationCloseDate.day", "")
                .param("url",""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = getBindingResult(
                mvcResult.getModelAndView().getModelMap(), "form"
        );

        assertTrue(bindingResult.getFieldError("url").getDefaultMessage().equals("Please enter a competition URL."));
        assertTrue(bindingResult.getFieldError("title").getDefaultMessage().equals("Please enter a title."));
        assertTrue(bindingResult.getFieldError("fundingType").getDefaultMessage().equals("Enter a valid funding type."));
        assertTrue(bindingResult.getFieldError("openDate").getDefaultMessage().equals("Please enter a valid date."));
        assertTrue(bindingResult.getFieldError("registrationCloseDate").getDefaultMessage().equals("Please enter a valid date."));
        assertTrue(bindingResult.getFieldError("closeDate").getDefaultMessage().equals("Please enter a valid date."));
        assertTrue(bindingResult.getFieldError("innovationSectorCategoryId").getDefaultMessage().equals("This field cannot be left blank."));
        assertTrue(bindingResult.getFieldError("innovationAreaCategoryId").getDefaultMessage().equals("This field cannot be left blank."));

        verifyZeroInteractions(nonIfsDetailsFormSaver);
    }

    @Test
    public void testSave_noParametersShouldReturnAppropriateErrors() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormSaver.save(any(), any())).thenReturn(serviceSuccess());

        MvcResult mvcResult = mockMvc.perform(post("/non-ifs-competition/setup/"+competitionId))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = getBindingResult(
                mvcResult.getModelAndView().getModelMap(), "form"
        );

        assertEquals("Please enter a competition URL.", bindingResult.getFieldError("url").getDefaultMessage());
        assertEquals("Please enter a title.",bindingResult.getFieldError("title").getDefaultMessage());
        assertEquals("Please enter an open date.", bindingResult.getFieldError("openDate").getDefaultMessage());
        assertEquals("Please enter a registration close date.", bindingResult.getFieldError("registrationCloseDate").getDefaultMessage());
        assertEquals("Please enter a competition close date.", bindingResult.getFieldError("closeDate").getDefaultMessage());
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("innovationSectorCategoryId").getDefaultMessage());
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("innovationAreaCategoryId").getDefaultMessage());

        verifyZeroInteractions(nonIfsDetailsFormSaver);
    }

    @Test
    public void testSave_yearFieldsUnderAndAboveLimitsAreRejectd() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormSaver.save(any(), any())).thenReturn(serviceSuccess());

        MvcResult mvcResult = mockMvc.perform(post("/non-ifs-competition/setup/"+competitionId)
                .param("openDate.year", "1999")
                .param("openDate.month", "")
                .param("openDate.day", "")
                .param("closeDate.year", "10000")
                .param("closeDate.month", "")
                .param("closeDate.day", "")
                .param("closeDate.time", "")
                .param("registrationCloseDate.year", "-1")
                .param("registrationCloseDate.month", "")
                .param("registrationCloseDate.day", ""))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = getBindingResult(
                mvcResult.getModelAndView().getModelMap(), "form"
        );

        assertEquals("Please enter a valid date.", bindingResult.getFieldError("openDate.year").getDefaultMessage());
        assertEquals("Please enter a valid date.", bindingResult.getFieldError("registrationCloseDate.year").getDefaultMessage());
        assertEquals("Please enter a valid date.", bindingResult.getFieldError("closeDate.year").getDefaultMessage());

        verifyZeroInteractions(nonIfsDetailsFormSaver);

    }

    @Test
    public void testSave_impossibleDatesShouldBeRejected() throws Exception {
        Long competitionId = 11L;

        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withNonIfs(true).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(nonIfsDetailsFormSaver.save(any(), any())).thenReturn(serviceSuccess());

        MvcResult mvcResult = mockMvc.perform(post("/non-ifs-competition/setup/"+competitionId)
                .param("openDate.year", "01")
                .param("openDate.month", "13")
                .param("openDate.day", "32")
                .param("closeDate.year", "2017")
                .param("closeDate.month", "-1")
                .param("closeDate.day", "01")
                .param("registrationCloseDate.year", "2017")
                .param("registrationCloseDate.month", "02")
                .param("registrationCloseDate.day", "29"))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        BindingResult bindingResult = getBindingResult(
                mvcResult.getModelAndView().getModelMap(), "form"
        );

        assertEquals("Please enter a valid date.", bindingResult.getFieldError("openDate").getDefaultMessage());
        assertEquals("Please enter a valid date.", bindingResult.getFieldError("registrationCloseDate").getDefaultMessage());
        assertEquals("Please enter a valid date.", bindingResult.getFieldError("closeDate").getDefaultMessage());

        verifyZeroInteractions(nonIfsDetailsFormSaver);
    }
}