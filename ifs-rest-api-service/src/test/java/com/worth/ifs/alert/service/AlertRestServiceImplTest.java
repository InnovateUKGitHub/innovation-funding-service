package com.worth.ifs.alert.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.alert.resource.AlertType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static com.worth.ifs.commons.service.ParameterizedTypeReferences.alertResourceListType;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class AlertRestServiceImplTest extends BaseRestServiceUnitTest<AlertRestServiceImpl> {

    private static final String alertRestURL = "/alert";

    @Override
    protected AlertRestServiceImpl registerRestServiceUnderTest() {
        final AlertRestServiceImpl alertRestService = new AlertRestServiceImpl();
        alertRestService.setAlertRestURL(alertRestURL);
        return alertRestService;
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void test_findAllVisible() throws Exception {
        final AlertResource expected1 = new AlertResource();
        expected1.setId(8888L);

        final AlertResource expected2 = new AlertResource();
        expected2.setId(9999L);

        final List<AlertResource> expected = asList(expected1, expected2);

        setupGetWithRestResultExpectations(alertRestURL + "/findAllVisible", alertResourceListType(), expected, OK);
        final List<AlertResource> response = service.findAllVisible().getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void test_findAllVisibleByType() throws Exception {
        final AlertResource expected1 = new AlertResource();
        expected1.setId(8888L);

        final AlertResource expected2 = new AlertResource();
        expected2.setId(9999L);

        final List<AlertResource> expected = asList(expected1, expected2);

        setupGetWithRestResultExpectations(alertRestURL + "/findAllVisible/MAINTENANCE", alertResourceListType(), expected, OK);
        final List<AlertResource> response = service.findAllVisibleByType(AlertType.MAINTENANCE).getSuccessObject();
        assertSame(expected, response);
    }

    @Test
    public void test_getAlertById() throws Exception {
        final AlertResource expected = new AlertResource();
        expected.setId(9999L);

        setupGetWithRestResultExpectations(alertRestURL + "/9999", AlertResource.class, expected, OK);
        final AlertResource response = service.getAlertById(9999L).getSuccessObject();
        Assert.assertEquals(expected, response);
    }

    @Test
    public void test_create() throws Exception {
        final AlertResource alertResource = sampleAlert();

        setupPostWithRestResultExpectations(alertRestURL + "/", AlertResource.class, alertResource, alertResource, CREATED);
        final AlertResource created = service.create(alertResource).getSuccessObject();
        setupPostWithRestResultVerifications(alertRestURL + "/", AlertResource.class, alertResource);
        Assert.assertEquals("Sample message", created.getMessage());
    }

    private AlertResource sampleAlert() {
        final AlertResource alert = new AlertResource();
        alert.setId(123L); //to check
        alert.setMessage("Sample message");
        alert.setType(AlertType.MAINTENANCE);
        alert.setValidFromDate(LocalDateTime.parse("2016-05-06T21:00:00.00"));
        alert.setValidToDate(LocalDateTime.parse("2016-05-06T21:05:00.00"));
        return alert;
    }

    @Test
    public void test_delete() throws Exception {
        setupDeleteWithRestResultExpectations(alertRestURL + "/9999");
        service.delete(9999L);
        setupDeleteWithRestResultVerifications(alertRestURL + "/9999");
    }

    @Test
    public void test_deleteAllByType() throws Exception {
        setupDeleteWithRestResultExpectations(alertRestURL + "/delete/MAINTENANCE");
        service.deleteAllByType(AlertType.MAINTENANCE);
        setupDeleteWithRestResultVerifications(alertRestURL + "/delete/MAINTENANCE");
    }
}