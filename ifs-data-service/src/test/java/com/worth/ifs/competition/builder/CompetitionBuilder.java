package com.worth.ifs.competition.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.idBasedNames;
import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
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

    public CompetitionBuilder withStartDate(LocalDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }
    public CompetitionBuilder withEndDate(LocalDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }
    public CompetitionBuilder withAssessmentStartDate(LocalDateTime startDate) {
        return with(competition -> setField("assessmentStartDate", startDate, competition));
    }
    public CompetitionBuilder withAssessmentEndDate(LocalDateTime endDate) {
        return with(competition -> setField("assessmentEndDate", endDate, competition));
    }
    public CompetitionBuilder withFundersPanelEndDate(LocalDateTime endDate) {
        return with(competition -> setField("fundersPanelEndDate", endDate, competition));
    }

    public CompetitionBuilder withActitiyCode(String activityCode) {
        return with(competition -> setField("activitiyCode", activityCode, competition));
    }

    public CompetitionBuilder withInnovateBudget(String innovateBudget) {
        return with(competition -> setField("innovateBudget", innovateBudget, competition));
    }

    public CompetitionBuilder withFunder(String funder) {
        return with(competition -> setField("funder", funder, competition));
    }

    public CompetitionBuilder withFunderBudget(Double funderBudget) {
        return with(competition -> setField("funderBudget", funderBudget, competition));
    }

    public CompetitionBuilder withName(String name) {
        return with(competition -> setField("name", name, competition));
    }
    public CompetitionBuilder withStatus(Status status) {
        return with(competition -> setField("status", status, competition));
    }

    public CompetitionBuilder withCompetitionStatus(Status status) {
        LocalDateTime now = LocalDateTime.now();
        if(Status.NOT_STARTED.equals(status)) {
    		return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.plusDays(1L));
    	} else if(Status.OPEN.equals(status)) {
    		return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(1L))
    				.withEndDate(now.plusDays(1L));
    	} else if(Status.CLOSED.equals(status)) {
            return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(2L))
                    .withEndDate(now.minusDays(1L))
    				.withAssessmentStartDate(now.plusDays(1L));
        } else if(Status.IN_ASSESSMENT.equals(status)) {
    		return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(2L))
    				.withEndDate(now.minusDays(1L))
    				.withAssessmentEndDate(now.plusDays(1L));
    	} else if(Status.FUNDERS_PANEL.equals(status)) {
    		return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(3L))
    				.withEndDate(now.minusDays(2L))
    				.withAssessmentEndDate(now.minusDays(1L))
    				.withFundersPanelEndDate(null);
    	} else if(Status.ASSESSOR_FEEDBACK.equals(status)) {
            return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(4L))
                    .withEndDate(now.minusDays(3L))
                    .withAssessmentEndDate(now.minusDays(2L))
                    .withFundersPanelEndDate(now.minusDays(1L));
        } else if(Status.PROJECT_SETUP.equals(status)) {
            return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.minusDays(5L))
                    .withEndDate(now.minusDays(4L))
                    .withAssessmentEndDate(now.minusDays(3L))
                    .withFundersPanelEndDate(now.minusDays(2L))
                    .withAssessorFeedbackDate(now.minusDays(1L));
        } else if(Status.COMPETITION_SETUP.equals(status)) {
            return withStatus(Status.COMPETITION_SETUP);
        } else if(Status.COMPETITION_SETUP_FINISHED.equals(status)) {
            return withStatus(Status.COMPETITION_SETUP_FINISHED)
                    .withStartDate(now.plusDays(1L));
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
