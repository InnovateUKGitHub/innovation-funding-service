package com.worth.ifs.alert.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.alert.resource.AlertResource;
import com.worth.ifs.application.service.AlertService;
import com.worth.ifs.application.service.AlertServiceImpl;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.alert.builder.AlertResourceBuilder.newAlertResource;
import static com.worth.ifs.alert.domain.AlertType.MAINTENANCE;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AlertServiceImplTest extends BaseServiceUnitTest<AlertService> {

    private static final Long ID_THAT_EXISTS = 9999L;

    @Mock
    private AlertRestService alertRestService;

    @Override
    protected AlertService supplyServiceUnderTest() {
        return new AlertServiceImpl();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

        when(alertRestService.delete(ID_THAT_EXISTS)).thenReturn(restSuccess());
        when(alertRestService.delete(not(eq(ID_THAT_EXISTS)))).thenReturn(restFailure(notFoundError(AlertResource.class)));
    }

    @Test
    public void test_findAllVisible() throws Exception {
        final AlertResource expected1 = newAlertResource()
                .withId(8888L)
                .build();

        final AlertResource expected2 = newAlertResource()
                .withId(9999L)
                .build();

        final List<AlertResource> expected = new ArrayList<>(asList(expected1, expected2));

        when(alertRestService.findAllVisible()).thenReturn(restSuccess(expected));

        final List<AlertResource> found = service.findAllVisible();
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(8888L), found.get(0).getId());
        assertEquals(Long.valueOf(9999L), found.get(1).getId());
    }

    @Test
    public void test_findAllVisibleByType() throws Exception {
        final AlertResource expected1 = newAlertResource()
                .withId(8888L)
                .build();

        final AlertResource expected2 = newAlertResource()
                .withId(9999L)
                .build();

        final List<AlertResource> expected = new ArrayList<>(asList(expected1, expected2));

        when(alertRestService.findAllVisibleByType(MAINTENANCE)).thenReturn(restSuccess(expected));

        final List<AlertResource> found = service.findAllVisibleByType(MAINTENANCE);
        assertEquals(2, found.size());
        assertEquals(Long.valueOf(8888L), found.get(0).getId());
        assertEquals(Long.valueOf(9999L), found.get(1).getId());
    }

    @Test
    public void test_getById() throws Exception {
        final AlertResource expected = newAlertResource()
                .withId(9999L)
                .build();

        when(alertRestService.getAlertById(9999L)).thenReturn(restSuccess(expected));

        final AlertResource alertResource = service.getById(9999L);
        assertEquals(expected, alertResource);
    }

    @Test
    public void test_create() throws Exception {
        final AlertResource alertResource =
                newAlertResource()
                .withMessage("Sample to be created")
                .withValidFromDate(LocalDateTime.parse("2016-05-06T21:00:00.00"))
                .withValidToDate(LocalDateTime.parse("2016-05-06T21:05:00.00"))
                .build();

        final AlertResource response =
                newAlertResource()
                .withId(9999L)
                .withMessage("Sample to be created")
                .withValidFromDate(LocalDateTime.parse("2016-05-06T21:00:00.00"))
                .withValidToDate(LocalDateTime.parse("2016-05-06T21:05:00.00"))
                .build();

        when(alertRestService.create(alertResource)).thenReturn(restSuccess(response));
        final AlertResource created = service.create(alertResource);
        verify(alertRestService, only()).create(alertResource);
        assertEquals(response, created);
    }

    @Test
    public void test_delete() throws Exception {
        service.delete(ID_THAT_EXISTS);
        verify(alertRestService, only()).delete(ID_THAT_EXISTS);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void test_delete_notExists() throws Exception {
        // try deleting any other id except the one which is known to exist
        service.delete(1L);
    }

    @Test
    public void test_deleteAllByType() throws Exception {
        when(alertRestService.deleteAllByType(MAINTENANCE)).thenReturn(restSuccess());
        service.deleteAllByType(MAINTENANCE);
        verify(alertRestService, only()).deleteAllByType(MAINTENANCE);
    }
}