package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.CompetitionType;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.resource.CompetitionStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.competition.resource.CompetitionStatus.*;
import static java.util.Collections.emptyList;

public class CompetitionBuilder extends BaseBuilder<Competition, CompetitionBuilder> {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private CompetitionBuilder(List<BiConsumer<Integer, Competition>> newMultiActions) {
        super(newMultiActions);
    }

    public static CompetitionBuilder newCompetition() {
        return new CompetitionBuilder(emptyList()).with(uniqueIds()).with(idBasedNames("Competition "));
    }

    public CompetitionBuilder withSections(List<Section> sections) {
        return with(competition -> competition.setSections(sections));
    }

    public CompetitionBuilder withSetupComplete(boolean setupComplete) {
        return with(competition -> setField("setupComplete", setupComplete, competition));
    }

    public CompetitionBuilder withStartDate(LocalDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }
    public CompetitionBuilder withEndDate(LocalDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionBuilder withAssessorAcceptsDate(LocalDateTime assessorAcceptsDate) {
        return with(competition -> setField("assessorAcceptsDate", assessorAcceptsDate, competition));
    }

    public CompetitionBuilder withAssessorDeadlineDate(LocalDateTime assessorDeadlineDate) {
        return with(competition -> setField("assessorDeadlineDate", assessorDeadlineDate, competition));
    }

    public CompetitionBuilder withFundersPanelDate(LocalDateTime fundersPanelDate) {
        return with(competition -> setField("fundersPanelDate", fundersPanelDate, competition));
    }

    public CompetitionBuilder withFundersPanelEndDate(LocalDateTime endDate) {
        return with(competition -> competition.setFundersPanelEndDate(endDate));
    }

    public CompetitionBuilder withResubmission(Boolean resubmission) {
        return with(competition -> setField("resubmission", resubmission, competition));
    }

    public CompetitionBuilder withCompetitionType(CompetitionType competitionType) {
        return with(competition -> setField("competitionType", competitionType, competition));
    }

    public CompetitionBuilder withActitiyCode(String activityCode) {
        return with(competition -> setField("activitiyCode", activityCode, competition));
    }

    public CompetitionBuilder withInnovateBudget(String innovateBudget) {
        return with(competition -> setField("innovateBudget", innovateBudget, competition));
    }

    public CompetitionBuilder withMilestones(List<Milestone> milestones){
        return with(competition -> competition.setMilestones(milestones));
    }

    public CompetitionBuilder withName(String... names) {
        return withArray((competition, name) -> setField("name", competition, name), names);
    }

    public CompetitionBuilder withStatus(CompetitionStatus status) {
        return with(competition -> setField("status", status, competition));
    }

    public CompetitionBuilder withAssessorsNotifiedDate(LocalDateTime... dates) {
        return withArray((date, competition) -> competition.notifyAssessors(date), dates);
    }

    public CompetitionBuilder withAssessmentClosedDate(LocalDateTime... dates) {
        return withArray((date, competition) -> competition.closeAssessment(date), dates);
    }

    public CompetitionBuilder withCompetitionStatus(CompetitionStatus status) {
        LocalDateTime now = LocalDateTime.now();
        if(READY_TO_OPEN.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.plusDays(1L));
    	} else if(OPEN.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(1L))
    				.withEndDate(now.plusDays(1L));
    	} else if(CLOSED.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(2L))
                    .withEndDate(now.minusDays(1L))
    				.withAssessorAcceptsDate(now.plusDays(1L));
        } else if(IN_ASSESSMENT.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(2L))
    				.withEndDate(now.minusDays(1L))
    				.withFundersPanelDate(now.plusDays(1L));
    	} else if(FUNDERS_PANEL.equals(status)) {
    		return withSetupComplete(true)
                    .withStartDate(now.minusDays(5L))
    				.withEndDate(now.minusDays(4L))
                    .withAssessorsNotifiedDate(now.minusDays(3L))
                    .withAssessmentClosedDate(now.minusDays(2L))
    				.withFundersPanelDate(now.minusDays(1L))
    				.withFundersPanelEndDate(null); // or now, or null?
    	} else if(ASSESSOR_FEEDBACK.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(4L))
                    .withEndDate(now.minusDays(3L))
                    .withFundersPanelDate(now.minusDays(2L))
                    .withFundersPanelEndDate(now.minusDays(1L));
        } else if(PROJECT_SETUP.equals(status)) {
            return withSetupComplete(true)
                    .withStartDate(now.minusDays(5L))
                    .withEndDate(now.minusDays(4L))
                    .withFundersPanelDate(now.minusDays(3L))
                    .withFundersPanelEndDate(now.minusDays(2L))
                    .withAssessorFeedbackDate(now.minusDays(1L));
        } else if(COMPETITION_SETUP.equals(status)) {
            return withSetupComplete(false);
        } else {
                throw new RuntimeException("status " + status + " not yet supported by CompetitionBuilder.withCompetitionStatus method");
            }
        }
    
    public CompetitionBuilder withAssessorFeedbackDate(LocalDateTime... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(date), endDate);
    }

    /**
     * Convenience method to set dates with format dd/MM/yyyy
     *
     * @param endDate
     * @return
     */
    public CompetitionBuilder withAssessorFeedbackDate(String... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(LocalDateTime.parse(date, DATE_FORMAT)), endDate);
    }

    @Override
    protected CompetitionBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Competition>> actions) {
        return new CompetitionBuilder(actions);
    }

    @Override
    protected Competition createInitial() {
        return new Competition();
    }

    public CompetitionBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

}
