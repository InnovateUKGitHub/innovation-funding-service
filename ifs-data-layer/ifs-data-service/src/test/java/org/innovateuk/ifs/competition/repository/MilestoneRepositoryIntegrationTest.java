package org.innovateuk.ifs.competition.repository;

import org.hibernate.validator.internal.util.CollectionHelper;
import org.innovateuk.ifs.BaseRepositoryIntegrationTest;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.innovateuk.ifs.competition.builder.MilestoneBuilder.newMilestone;
import static org.junit.Assert.assertThat;

public class MilestoneRepositoryIntegrationTest extends BaseRepositoryIntegrationTest<MilestoneRepository> {

    @Autowired
    @Override
    protected void setRepository(MilestoneRepository repository) {
        this.repository = repository;
    }

    @Test
    public void testTimeZones() {
        ZonedDateTime utc = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"));
        Milestone utcMilestone = newMilestone().withDate(utc).build();
        Milestone actMilestone = newMilestone().withDate(utc.withZoneSameInstant(ZoneId.of("Australia/Darwin"))).build();
        Milestone ectMilestone = newMilestone().withDate(utc.withZoneSameInstant(ZoneId.of("Europe/Paris"))).build();
        Milestone betMilestone = newMilestone().withDate(utc.withZoneSameInstant(ZoneId.of("America/Sao_Paulo"))).build();

        Long utcId = repository.save(utcMilestone).getId();
        Long actId = repository.save(actMilestone).getId();
        Long ectId = repository.save(ectMilestone).getId();
        Long betId = repository.save(betMilestone).getId();

        flushAndClearSession();

        //Assert that the dates are read out of the database in the same timezone and are all equal.
        Set<ZonedDateTime> uniqueSet = CollectionHelper.asSet(repository.findOne(utcId).getDate(),
                repository.findOne(actId).getDate(),
                repository.findOne(ectId).getDate(),
                repository.findOne(betId).getDate());

        assertThat(uniqueSet.size(), equalTo(1));

    }

}
