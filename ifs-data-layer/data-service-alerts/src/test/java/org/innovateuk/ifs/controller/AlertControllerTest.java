package org.innovateuk.ifs.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.transactional.AlertService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.innovateuk.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class AlertControllerTest extends BaseControllerMockMVCTest<AlertController> {


    @Mock
    private AlertService alertServiceMock;

    @Before
    public void setUp() {
    }

    @Override
    protected AlertController supplyControllerUnderTest() {
        return new AlertController();
    }

    @Test
    public void test_findAllVisible() throws Exception {
        final AlertResource expected1 = AlertResourceBuilder.newAlertResource()
                .withId(8888L)
                .build();

        final AlertResource expected2 = AlertResourceBuilder.newAlertResource()
                .withId(9999L)
                .build();

        final List<AlertResource> expected = new ArrayList<>(asList(expected1, expected2));

        when(alertServiceMock.findAllVisible()).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/alert/find-all-visible"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(containsString("8888")))
                .andExpect(content().string(containsString("9999")));
    }

    @Test
    public void test_findAllVisibleByType() throws Exception {
        final AlertResource expected1 = AlertResourceBuilder.newAlertResource()
                .withId(8888L)
                .build();

        final AlertResource expected2 = AlertResourceBuilder.newAlertResource()
                .withId(9999L)
                .build();

        final List<AlertResource> expected = new ArrayList<>(asList(expected1, expected2));

        when(alertServiceMock.findAllVisibleByType(MAINTENANCE)).thenReturn(serviceSuccess(expected));


        mockMvc.perform(get("/alert/find-all-visible/{type}", MAINTENANCE.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().string(containsString("8888")))
                .andExpect(content().string(containsString("9999")));
    }

    @Test
    public void test_getById() throws Exception {
        final AlertResource expected = AlertResourceBuilder.newAlertResource()
                .withId(9999L)
                .build();

        when(alertServiceMock.findById(9999L)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/alert/{id}", 9999L))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void test_create() throws Exception {
        final AlertResource alertResource = AlertResourceBuilder.newAlertResource()
                .build();

        final AlertResource expected = AlertResourceBuilder.newAlertResource()
                .withId(9999L)
                .build();

        when(alertServiceMock.create(alertResource)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/alert/")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alertResource)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void test_delete() throws Exception {
        when(alertServiceMock.delete(9999L)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/alert/{id}", 9999L))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));
    }


    @Test
    public void test_deleteAllByType() throws Exception {
        when(alertServiceMock.deleteAllByType(MAINTENANCE)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/alert/delete/{type}", MAINTENANCE.name()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(isEmptyString()));
    }
}
