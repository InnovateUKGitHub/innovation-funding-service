package org.innovateuk.ifs.alert.repository;

import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.alert.domain.Alert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.junit.Assert.*;

public class AlertRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AlertRepository> {

    @Autowired
    @Override
    protected void setRepository(AlertRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findAll() throws Exception {
        List<Alert> found = repository.findAll();

        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    @Rollback
    public void test_findAllVisible() throws Exception {
        // save new alerts with date ranges that should make them visible now
        ZonedDateTime now = now();
        ZonedDateTime oneSecondAgo = now.minusSeconds(1);
        ZonedDateTime oneDayAgo = now.minusDays(1);
        ZonedDateTime oneHourAhead = now.plusHours(1);
        ZonedDateTime oneDayAhead = now.plusDays(1);

        Alert visible1 = new Alert("Sample message", MAINTENANCE, oneDayAgo, oneDayAhead);
        Alert visible2 = new Alert("Sample message", MAINTENANCE, oneSecondAgo, oneHourAhead);

        Alert expected1 = repository.save(visible1);
        Alert expected2 = repository.save(visible2);

        List<Alert> found = repository.findAllVisible(now);

        assertEquals(2, found.size());
        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
    }

    @Test
    @Rollback
    public void test_findAllVisibleByType() throws Exception {
        // save new alerts with date ranges that should make them visible now
        ZonedDateTime now = now();
        ZonedDateTime oneSecondAgo = now.minusSeconds(1);
        ZonedDateTime oneDayAgo = now.minusDays(1);
        ZonedDateTime oneHourAhead = now.plusHours(1);
        ZonedDateTime oneDayAhead = now.plusDays(1);

        Alert visible1 = new Alert("Sample message", MAINTENANCE, oneDayAgo, oneDayAhead);
        Alert visible2 = new Alert("Sample message", MAINTENANCE, oneSecondAgo, oneHourAhead);

        Alert expected1 = repository.save(visible1);
        Alert expected2 = repository.save(visible2);

        List<Alert> found = repository.findAllVisibleByType(MAINTENANCE, now);

        assertEquals(2, found.size());

        assertFalse(found.stream()
                .filter(a -> !MAINTENANCE.equals(a.getType()))
                .findAny()
                .isPresent()
        );

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
    }

    @Test
    public void test_findOne() throws Exception {
        Long id = 1L;
        Alert alert = repository.findOne(id);

        assertEquals(id, alert.getId());
        assertEquals("Sample message", alert.getMessage());
        assertEquals(MAINTENANCE, alert.getType());
        assertEquals(LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), alert.getValidFromDate());
        assertEquals(LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()), alert.getValidToDate());
    }

    @Test
    @Rollback
    public void test_save() throws Exception {
        Alert alertResource = new Alert("Sample message for save", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()));
        Alert saved = repository.save(alertResource);

        assertNotNull(saved.getId());
        assertEquals(alertResource, repository.findOne(saved.getId()));
    }

    @Test
    @Rollback
    public void test_delete() throws Exception {
        // save a new alert
        Alert alertResource = new Alert("Sample message for delete", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()));
        Alert saved = repository.save(alertResource);

        // check that it can be found
        assertNotNull(saved.getId());
        assertEquals(alertResource, repository.findOne(saved.getId()));

        // now delete it
        repository.delete(saved.getId());

        // make sure it can't be found
        Alert expectedNotFound = repository.findOne(saved.getId());
        assertNull(expectedNotFound);
    }

    @Test
    @Rollback
    public void test_deleteByType() throws Exception {
        repository.deleteByType(MAINTENANCE);

        List<Alert> alerts = repository.findAll();
        assertFalse(alerts.stream()
                .filter(a -> MAINTENANCE.equals(a.getType()))
                .findAny()
                .isPresent()
        );
    }
}
