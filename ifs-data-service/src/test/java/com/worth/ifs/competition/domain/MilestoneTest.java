package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.resource.MilestoneType;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MilestoneTest {

    private Milestone milestone;
    private Competition competition;
    private List<String> milestoneTypes;

    private Long id;
    private MilestoneType type;
    private LocalDateTime date;
    private Long competitionId;

    private String openDate = "OPEN_DATE";
    private String briefingEvent = "BRIEFING_EVENT";
    private String submissionDate = "SUBMISSION_DATE";
    private String allocateAssessors = "ALLOCATE_ASSESSORS";
    private String assessorBriefing = "ASSESSOR_BRIEFING";
    private String assessorAccepts = "ASSESSOR_ACCEPTS";
    private String assessorDeadline = "ASSESSOR_DEADLINE";
    private String lineDraw = "LINE_DRAW";
    private String assessmentPanel = "ASSESSMENT_PANEL";
    private String panelDate = "PANEL_DATE";
    private String fundersPanel = "FUNDERS_PANEL";
    private String notifications = "NOTIFICATIONS";
    private String releaseFeedback = "RELEASE_FEEDBACK";

    @Before
    public void setUp() throws Exception {
        id = 0L;
        type = MilestoneType.OPEN_DATE;
        date = LocalDateTime.now().plusDays(7);
        competitionId = 1L;

        competition = new Competition();
        competition.setId(competitionId);

        milestone = new Milestone(type, date, competition);
        populateMilestoneTypes();
    }

    @Test
    public void getMilestone() {
        assertEquals(milestone.getType(), type);
        assertEquals(milestone.getDate(), date);
        assertEquals(milestone.getCompetition().getId(), competitionId);
    }

    @Test
    public void milestoneTypeSize() {
        assertTrue(MilestoneType.values().length == 15);

        List<String> milestoneEnum = new ArrayList<>();

        Stream.of(MilestoneType.values()).forEach(name -> {
            milestoneEnum.add(name.toString());
        });
        assertTrue(!Collections.disjoint(milestoneTypes, milestoneEnum));
    }

    private void populateMilestoneTypes(){
        milestoneTypes = new ArrayList<>();
        milestoneTypes.add(openDate);
        milestoneTypes.add(briefingEvent);
        milestoneTypes.add(submissionDate);
        milestoneTypes.add(allocateAssessors);
        milestoneTypes.add(assessorBriefing);
        milestoneTypes.add(assessorAccepts);
        milestoneTypes.add(assessorDeadline);
        milestoneTypes.add(lineDraw);
        milestoneTypes.add(assessmentPanel);
        milestoneTypes.add(panelDate);
        milestoneTypes.add(fundersPanel);
        milestoneTypes.add(notifications);
        milestoneTypes.add(releaseFeedback);
    }
}
