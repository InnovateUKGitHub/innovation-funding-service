package org.innovateuk.ifs.alert;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AlertControllerTest extends BaseControllerMockMVCTest<AlertController> {

    @Override
    protected AlertController supplyControllerUnderTest() {
        return new AlertController();
    }

    @Test
    public void findAllVisibleByType() throws Exception {
        when(alertRestService.findAllVisibleByType(AlertType.MAINTENANCE))
                .thenReturn(
                        restSuccess(asList(newAlertResource()
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
