package com.worth.ifs.alert.repository;

import com.worth.ifs.BaseRepositoryIntegrationTest;
import com.worth.ifs.alert.domain.Alert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.junit.Assert.*;

public class AlertRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<AlertRepository> {

    @Autowired
    @Override
    protected void setRepository(final AlertRepository repository) {
        this.repository = repository;
    }

    @Test
    public void test_findAll() throws Exception {
        final List<Alert> found = repository.findAll();

        assertEquals(2, found.size());
        assertEquals(Long.valueOf(1L), found.get(0).getId());
        assertEquals(Long.valueOf(2L), found.get(1).getId());
    }

    @Test
    @Rollback
    public void test_findAllVisible() throws Exception {
        // save new alerts with date ranges that should make them visible now
        final LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        final LocalDateTime oneSecondAgo = now.minusSeconds(1);
        final LocalDateTime oneDayAgo = now.minusDays(1);
        final LocalDateTime oneHourAhead = now.plusHours(1);
        final LocalDateTime oneDayAhead = now.plusDays(1);

        final Alert visible1 = new Alert("Sample message", MAINTENANCE, oneDayAgo, oneDayAhead);
        final Alert visible2 = new Alert("Sample message", MAINTENANCE, oneSecondAgo, oneHourAhead);

        final Alert expected1 = repository.save(visible1);
        final Alert expected2 = repository.save(visible2);

        final List<Alert> found = repository.findAllVisible();

        assertEquals(2, found.size());
        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
    }

    @Test
    @Rollback
    public void test_findAllVisibleByType() throws Exception {
        // save new alerts with date ranges that should make them visible now
        final LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        final LocalDateTime oneSecondAgo = now.minusSeconds(1);
        final LocalDateTime oneDayAgo = now.minusDays(1);
        final LocalDateTime oneHourAhead = now.plusHours(1);
        final LocalDateTime oneDayAhead = now.plusDays(1);

        final Alert visible1 = new Alert("Sample message", MAINTENANCE, oneDayAgo, oneDayAhead);
        final Alert visible2 = new Alert("Sample message", MAINTENANCE, oneSecondAgo, oneHourAhead);

        final Alert expected1 = repository.save(visible1);
        final Alert expected2 = repository.save(visible2);

        final List<Alert> found = repository.findAllVisibleByType(MAINTENANCE);

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
        final Long id = 1L;
        final Alert alert = repository.findOne(id);

        assertEquals(id, alert.getId());
        assertEquals("Sample message", alert.getMessage());
        assertEquals(MAINTENANCE, alert.getType());
        assertEquals(LocalDateTime.parse("2016-05-06T21:00:00.00"), alert.getValidFromDate());
        assertEquals(LocalDateTime.parse("2016-05-06T21:05:00.00"), alert.getValidToDate());
    }

    @Test
    @Rollback
    public void test_save() throws Exception {
        final Alert alertResource = new Alert("Sample message for save", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00"), LocalDateTime.parse("2016-05-06T21:05:00.00"));
        final Alert saved = repository.save(alertResource);

        assertNotNull(saved.getId());
        assertEquals(alertResource, repository.findOne(saved.getId()));
    }

    @Test
    @Rollback
    public void test_delete() throws Exception {
        // save a new alert
        final Alert alertResource = new Alert("Sample message for delete", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00"), LocalDateTime.parse("2016-05-06T21:05:00.00"));
        final Alert saved = repository.save(alertResource);

        // check that it can be found
        assertNotNull(saved.getId());
        assertEquals(alertResource, repository.findOne(saved.getId()));

        // now delete it
        repository.delete(saved.getId());

        // make sure it can't be found
        final Alert expectedNotFound = repository.findOne(saved.getId());
        assertNull(expectedNotFound);
    }

    @Test
    @Rollback
    public void test_deleteByType() throws Exception {
        repository.deleteByType(MAINTENANCE);

        final List<Alert> alerts = repository.findAll();
        assertFalse(alerts.stream()
                .filter(a -> MAINTENANCE.equals(a.getType()))
                .findAny()
                .isPresent()
        );
    }
}
