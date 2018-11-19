package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form.YourProjectLocationFormPopulator;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel.YourProjectLocationViewModelPopulator;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.AsyncTestExpectationHelper.setupAsyncExpectations;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class YourProjectLocationControllerTest extends BaseControllerMockMVCTest<YourProjectLocationController> {

    @Mock
    private YourProjectLocationViewModelPopulator viewModelPopulatorMock;

    @Mock
    private YourProjectLocationFormPopulator formPopulatorMock;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestServiceMock;

    @Mock
    private SectionService sectionServiceMock;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private AsyncFuturesGenerator futuresGeneratorMock;

    private long applicationId = 123L;
    private long sectionId = 456L;
    private long organisationId = 789L;

    @Before
    public void setupExpectations() {
        setupAsyncExpectations(futuresGeneratorMock);
    }

    @Test
    public void viewPage() throws Exception {

        boolean internalUser = false;

        YourProjectLocationViewModel viewModel =
                new YourProjectLocationViewModel(false, "", "", applicationId, sectionId, true);

        YourProjectLocationForm form = new YourProjectLocationForm("S2 5AB");

        when(viewModelPopulatorMock.populate(organisationId, applicationId, sectionId, internalUser)).thenReturn(viewModel);
        when(formPopulatorMock.populate(applicationId, organisationId)).thenReturn(form);

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/sections/your-project-location/your-project-location"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        assertThat(model.get("model")).matches(futureMatcher(viewModel));
        assertThat(model.get("form")).matches(futureMatcher(form));

        verify(viewModelPopulatorMock, times(1)).populate(organisationId, applicationId, sectionId, internalUser);
        verify(formPopulatorMock, times(1)).populate(applicationId, organisationId);
    }


    @Test
    public void update() throws Exception {

        String postcode = "S2 5AB";

        ApplicationFinanceResource applicationFinance = newApplicationFinanceResource().build();

        when(applicationFinanceRestServiceMock.getApplicationFinance(applicationId, organisationId)).thenReturn(
                restSuccess(applicationFinance));

        ArgumentCaptor<ApplicationFinanceResource> updatedApplicationFinanceCaptor = new ArgumentCaptor<>();

        when(applicationFinanceRestServiceMock.update(eq(applicationFinance.getId()), updatedApplicationFinanceCaptor.capture())).thenReturn(
                restSuccess(applicationFinance));

        mockMvc.perform(post("/application/{applicationId}/form/your-project-location/" +
                "organisation/{organisationId}/section/{sectionId}", applicationId, organisationId, sectionId)
                    .param("postcode", postcode))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(String.format("redirect:/application/%d/form/FINANCE", applicationId)))
                .andReturn();

        ApplicationFinanceResource applicationFinanceBeingUpdated = updatedApplicationFinanceCaptor.getValue();
        assertThat(applicationFinanceBeingUpdated.getWorkPostcode()).isEqualTo(postcode);

        verify(applicationFinanceRestServiceMock, times(1)).getApplicationFinance(applicationId, organisationId);
        verify(applicationFinanceRestServiceMock, times(1)).update(applicationFinance.getId(), applicationFinance);
    }

    private Predicate<Object> futureMatcher(Object object) {

        return value -> {

            if (!(value instanceof CompletableFuture)) {
                return false;
            }

            CompletableFuture future = (CompletableFuture) value;

            try {
                return future.get() == object;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    protected YourProjectLocationController supplyControllerUnderTest() {

        return new YourProjectLocationController(
                viewModelPopulatorMock,
                formPopulatorMock,
                applicationFinanceRestServiceMock,
                sectionServiceMock,
                userRestServiceMock);
    }
}
