package org.innovateuk.ifs.repository;

import org.innovateuk.ifs.domain.Alert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.alert.resource.AlertType.MAINTENANCE;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("integration-test")
public class AlertRepositoryIntegrationTest {

    @Autowired
    private AlertRepository repository;

    @Test
    @Rollback
    public void findAllVisible() {
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
    public void findAllVisibleByType() {
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

        assertFalse(found.stream().anyMatch(a -> !MAINTENANCE.equals(a.getType())));

        assertEquals(expected1, found.get(0));
        assertEquals(expected2, found.get(1));
    }

    @Test
    @Rollback
    public void save() {
        Alert alertResource = new Alert("Sample message for save", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()));
        Alert saved = repository.save(alertResource);

        assertNotNull(saved.getId());
        Assert.assertEquals(alertResource, repository.findById(saved.getId()).get());
    }

    @Test
    @Rollback
    public void delete() {
        // save a new alert
        Alert alertResource = new Alert("Sample message for delete", MAINTENANCE, LocalDateTime.parse("2016-05-06T21:00:00.00").atZone(ZoneId.systemDefault()), LocalDateTime.parse("2016-05-06T21:05:00.00").atZone(ZoneId.systemDefault()));
        Alert saved = repository.save(alertResource);

        // check that it can be found
        assertNotNull(saved.getId());
        Assert.assertEquals(alertResource, repository.findById(saved.getId()).get());

        // now delete it
        repository.deleteById(saved.getId());

        // make sure it can't be found
        Optional<Alert> expectedNotFound = repository.findById(saved.getId());
        assertFalse(expectedNotFound.isPresent());
    }

    @Test
    @Rollback
    public void deleteByType() {
        repository.deleteByType(MAINTENANCE);

        List<Alert> alerts = repository.findAll();
        assertFalse(alerts.stream().anyMatch(a -> MAINTENANCE.equals(a.getType()))
        );
    }

}
