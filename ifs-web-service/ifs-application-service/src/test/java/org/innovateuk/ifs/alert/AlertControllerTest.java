package org.innovateuk.ifs.alert;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.innovateuk.ifs.alert.service.AlertRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AlertControllerTest extends BaseControllerMockMVCTest<AlertController> {

    @Mock
    private AlertRestService alertRestService;

    @Override
    protected AlertController supplyControllerUnderTest() {
        return new AlertController();
    }

    @Test
    public void findAllVisibleByType() throws Exception {
        when(alertRestService.findAllVisibleByType(AlertType.MAINTENANCE))
                .thenReturn(
                        RestResult.restSuccess(Arrays.asList(AlertResourceBuilder.newAlertResource()
                                .withId(1L)
                                .withMessage("Test Maintenance")
                                .withType(AlertType.MAINTENANCE)
                                .withValidFromDate(ZonedDateTime.now().minusDays(2))
                                .withValidToDate(ZonedDateTime.now().plusDays(2))
                                .build()))
                );

        mockMvc.perform(get("/alert/findAllVisibleByType/" + AlertType.MAINTENANCE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[0]message", is("Test Maintenance")))
                .andExpect(jsonPath("[0]type", is(AlertType.MAINTENANCE.toString())));
    }
}
