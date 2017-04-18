package org.innovateuk.ifs.alert;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.alert.resource.AlertType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource(locations = "classpath:application.properties")
public class AlertControllerTest extends BaseUnitTest {

    @InjectMocks
    private AlertController alertController;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        mockMvc = setupMockMvc(alertController, () -> loggedInUser, env, messageSource);

        super.setup();

        when(alertService.findAllVisibleByType(AlertType.MAINTENANCE))
                .thenReturn(
                        asList(newAlertResource()
                                .withId(1L)
                                .withMessage("Test Maintenance")
                                .withType(AlertType.MAINTENANCE)
                                .withValidFromDate(ZonedDateTime.now().minusDays(2))
                                .withValidToDate(ZonedDateTime.now().plusDays(2))
                                .build())
                );
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAlertMessage() throws Exception {
        mockMvc.perform(get("/alert/findAllVisibleByType/" + AlertType.MAINTENANCE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("[0]message", is("Test Maintenance")))
                .andExpect(jsonPath("[0]type", is(AlertType.MAINTENANCE.toString())));
    }
}
