package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.alert.builder.AlertResourceBuilder;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.domain.Alert;
import org.innovateuk.ifs.mapper.AlertMapper;
import org.innovateuk.ifs.repository.AlertRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AlertServiceImplTest extends BaseUnitTestMocksTest {

    @Mock
    private AlertRepository alertRepositoryMock;

    @Mock
    private AlertMapper alertMapperMock;

    @InjectMocks
    private final AlertService alertService = new AlertServiceImpl();

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void findAllVisible() throws Exception {
        final Alert alert1 = new Alert();
        final Alert alert2 = new Alert();

        final List<Alert> alerts = new ArrayList<>(asList(alert1, alert2));

        final AlertResource expected1 = AlertResourceBuilder.newAlertResource()
                .build();

        final AlertResource expected2 = AlertResourceBuilder.newAlertResource()
                .build();

        when(alertRepositoryMock.findAllVisible(isA(ZonedDateTime.class))).thenReturn(alerts);
        when(alertMapperMock.mapToResource(same(alert1))).thenReturn(expected1);
        when(alertMapperMock.mapToResource(same(alert2))).thenReturn(expected2);

        final List<AlertResource> found = alertService.findAllVisible().getSuccess();

        assertSame(expected1, found.get(0));
        assertSame(expected2, found.get(1));
        verify(alertRepositoryMock, only()).findAllVisible(isA(ZonedDateTime.class));
    }

    @Test
    public void findAllVisibleByType() throws Exception {
        final Alert alert1 = new Alert();
        final Alert alert2 = new Alert();

        final List<Alert> alerts = new ArrayList<>(asList(alert1, alert2));

        final AlertResource expected1 = AlertResourceBuilder.newAlertResource()
                .build();

        final AlertResource expected2 = AlertResourceBuilder.newAlertResource()
                .build();

        when(alertRepositoryMock.findAllVisibleByType(same(MAINTENANCE), isA(ZonedDateTime.class))).thenReturn(alerts);
        when(alertMapperMock.mapToResource(same(alert1))).thenReturn(expected1);
        when(alertMapperMock.mapToResource(same(alert2))).thenReturn(expected2);

        final List<AlertResource> found = alertService.findAllVisibleByType(MAINTENANCE).getSuccess();

        assertSame(expected1, found.get(0));
        assertSame(expected2, found.get(1));
        verify(alertRepositoryMock, only()).findAllVisibleByType(same(MAINTENANCE), isA(ZonedDateTime.class));
    }

    @Test
    public void findById() throws Exception {
        final AlertResource expected = AlertResourceBuilder.newAlertResource()
                .build();

        final Alert alert = new Alert();
        when(alertRepositoryMock.findOne(9999L)).thenReturn(alert);
        when(alertMapperMock.mapToResource(same(alert))).thenReturn(expected);

        assertSame(expected, alertService.findById(9999L).getSuccess());
        verify(alertRepositoryMock, only()).findOne(9999L);
    }

    @Test
    public void create() throws Exception {
        final AlertResource alertResource = AlertResourceBuilder.newAlertResource()
                .build();

        final AlertResource expected = AlertResourceBuilder.newAlertResource()
                .build();

        final Alert alert = new Alert();
        when(alertMapperMock.mapToDomain(same(alertResource))).thenReturn(alert);
        when(alertRepositoryMock.save(same(alert))).thenReturn(alert);
        when(alertMapperMock.mapToResource(same(alert))).thenReturn(expected);

        assertSame(expected, alertService.create(alertResource).getSuccess());
        verify(alertRepositoryMock, only()).save(alert);

    }

    @Test
    public void delete() throws Exception {
        assertTrue(alertService.delete(9999L).isSuccess());
        verify(alertRepositoryMock, only()).delete(9999L);
    }

    @Test
    public void deleteAllByType() throws Exception {
        assertTrue(alertService.delete(9999L).isSuccess());
    }
}
