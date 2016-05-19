package com.worth.ifs.alert;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.resource.AlertType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
        mockMvc = MockMvcBuilders.standaloneSetup(alertController)
                .setViewResolvers(viewResolver())
                .build();

        super.setup();

        List<AlertResource> alerts = new ArrayList<AlertResource>();
        alerts.add(new AlertResource(1L, "Test Maintenance", AlertType.MAINTENANCE, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2)));

        when(alertService.findAllVisibleByType(AlertType.MAINTENANCE)).thenReturn(alerts);
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