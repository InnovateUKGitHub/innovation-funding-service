package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MilestoneTest {

    @Test
    public void getMilestone() {
        MilestoneType type = MilestoneType.OPEN_DATE;
        ZonedDateTime date = ZonedDateTime.now().plusDays(7);
        Long competitionId = 1L;

        Competition competition = newCompetition().withId(competitionId).build();
        competition.setId(competitionId);

        Milestone milestone = new Milestone(type, date, competition);

        assertEquals(milestone.getType(), type);
        assertEquals(milestone.getDate(), date);
        assertEquals(milestone.getCompetition().getId(), competitionId);
    }

    @Test
    public void milestoneTypeSize() {
        assertEquals(16, MilestoneType.values().length);
        assertEquals(13, MilestoneType.presetValues().length);
    }

    @Test
    public void create_presetMilestone() {
        MilestoneType milestoneType = Stream.of(MilestoneType.presetValues()).findFirst().get();
        new Milestone(milestoneType, ZonedDateTime.now(), newCompetition().build());
    }

    @Test(expected = NullPointerException.class)
    public void create_presetMilestoneWithNoDate() {
        MilestoneType milestoneType = Stream.of(MilestoneType.presetValues()).findFirst().get();
        new Milestone(milestoneType, newCompetition().build());
    }

    @Test
    public void create_nonPresetMilestone() {
        MilestoneType milestoneType = Stream.of(MilestoneType.values()).filter(t -> !t.isPresetDate()).findFirst().get();
        new Milestone(milestoneType, ZonedDateTime.now(), newCompetition().build());
    }

    @Test
    public void create_nonPresetMilestoneWithNoDate() {
        MilestoneType milestoneType = Stream.of(MilestoneType.values()).filter(t -> !t.isPresetDate()).findFirst().get();
        new Milestone(milestoneType, newCompetition().build());
    }

    @Test
    public void isSet() {
        Milestone assessorsNotifiedMilestone = new Milestone(MilestoneType.ASSESSORS_NOTIFIED, newCompetition().build());
        assertFalse(assessorsNotifiedMilestone.isSet());
        assessorsNotifiedMilestone.setDate(ZonedDateTime.now());
        assertTrue(assessorsNotifiedMilestone.isSet());
    }

    @Test
    public void isReached() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime future = now.plusNanos(1);
        ZonedDateTime past = now.minusNanos(1);

        assertFalse( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, future, newCompetition().build()).isReached(now) );
        assertTrue( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, now, newCompetition().build()).isReached(now) );
        assertTrue( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, past, newCompetition().build()).isReached(now) );
    }
}
