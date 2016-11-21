package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MilestoneTest {

    @Test
    public void getMilestone() {
        MilestoneType type = MilestoneType.OPEN_DATE;
        LocalDateTime date = LocalDateTime.now().plusDays(7);
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
        assertEquals(15, MilestoneType.values().length);
        assertEquals(13, MilestoneType.presetValues().length);
    }

    @Test
    public void create_presetMilestone() {
        MilestoneType milestoneType = Stream.of(MilestoneType.presetValues()).findFirst().get();
        new Milestone(milestoneType, LocalDateTime.now(), newCompetition().build());
    }

    @Test(expected = NullPointerException.class)
    public void create_presetMilestoneWithNoDate() {
        MilestoneType milestoneType = Stream.of(MilestoneType.presetValues()).findFirst().get();
        new Milestone(milestoneType, newCompetition().build());
    }

    @Test
    public void create_nonPresetMilestone() {
        MilestoneType milestoneType = Stream.of(MilestoneType.values()).filter(t -> !t.isPresetDate()).findFirst().get();
        new Milestone(milestoneType, LocalDateTime.now(), newCompetition().build());
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
        assessorsNotifiedMilestone.setDate(LocalDateTime.now());
        assertTrue(assessorsNotifiedMilestone.isSet());
    }

    @Test
    public void isReached() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusNanos(1);
        LocalDateTime past = now.minusNanos(1);

        assertFalse( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, future, newCompetition().build()).isReached(now) );
        assertTrue( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, now, newCompetition().build()).isReached(now) );
        assertTrue( new Milestone(MilestoneType.ALLOCATE_ASSESSORS, past, newCompetition().build()).isReached(now) );
    }
}
