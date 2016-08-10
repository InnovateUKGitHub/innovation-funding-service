package com.worth.ifs.competition.domain;

import com.worth.ifs.competition.mapper.MilestoneMapper;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
    private List<String> milestoneNames;

    private Long id;
    private MilestoneName name;
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
        name = MilestoneName.OPEN_DATE;
        date = LocalDateTime.now().plusDays(7);
        competitionId = 1L;

        competition = new Competition();
        competition.setId(competitionId);

        milestone = new Milestone(id, name, date, competition);
        populateMilestoneNames();
    }

    @Test
    public void getMilestone() {
        assertEquals(milestone.getId(), id);
        assertEquals(milestone.getName(), name);
        assertEquals(milestone.getDate(), date);
        assertEquals(milestone.getCompetition().getId(), competitionId);
    }

    @Test
    public void milestoneNameSize() {
        assertTrue(MilestoneName.values().length == 13);

        List<String> milestoneEnum = new ArrayList<>();

        Stream.of(MilestoneName.values()).forEach(name -> {
            milestoneEnum.add(name.toString());
        });
        assertTrue(!Collections.disjoint(milestoneNames, milestoneEnum));
    }

    private void populateMilestoneNames(){
        milestoneNames = new ArrayList<>();
        milestoneNames.add(openDate);
        milestoneNames.add(briefingEvent);
        milestoneNames.add(submissionDate);
        milestoneNames.add(allocateAssessors);
        milestoneNames.add(assessorBriefing);
        milestoneNames.add(assessorAccepts);
        milestoneNames.add(assessorDeadline);
        milestoneNames.add(lineDraw);
        milestoneNames.add(assessmentPanel);
        milestoneNames.add(panelDate);
        milestoneNames.add(fundersPanel);
        milestoneNames.add(notifications);
        milestoneNames.add(releaseFeedback);
    }
}
