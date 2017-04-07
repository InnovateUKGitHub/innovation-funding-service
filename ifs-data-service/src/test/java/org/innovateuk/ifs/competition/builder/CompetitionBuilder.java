package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.category.domain.InnovationSector;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

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

    public CompetitionBuilder withStartDate(ZonedDateTime startDate) {
        return with(competition -> setField("startDate", startDate, competition));
    }
    public CompetitionBuilder withEndDate(ZonedDateTime endDate) {
        return with(competition -> setField("endDate", endDate, competition));
    }

    public CompetitionBuilder withAssessorAcceptsDate(ZonedDateTime assessorAcceptsDate) {
        return with(competition -> setField("assessorAcceptsDate", assessorAcceptsDate, competition));
    }

    public CompetitionBuilder withAssessorDeadlineDate(ZonedDateTime assessorDeadlineDate) {
        return with(competition -> setField("assessorDeadlineDate", assessorDeadlineDate, competition));
    }

    public CompetitionBuilder withReleaseFeedbackDate(ZonedDateTime releaseFeedbackDate) {
        return with(competition -> setField("releaseFeedbackDate", releaseFeedbackDate, competition));
    }

    public CompetitionBuilder withFundersPanelDate(ZonedDateTime fundersPanelDate) {
        return with(competition -> setField("fundersPanelDate", fundersPanelDate, competition));
    }

    public CompetitionBuilder withFundersPanelEndDate(ZonedDateTime endDate) {
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

    public CompetitionBuilder withFullFinance(boolean fullFinance) {
        return with(competition -> setField("fullApplicationFinance", fullFinance, competition));
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

    public CompetitionBuilder withAssessorsNotifiedDate(ZonedDateTime... dates) {
        return withArray((date, competition) -> competition.notifyAssessors(date), dates);
    }

    public CompetitionBuilder withAssessmentClosedDate(ZonedDateTime... dates) {
        return withArray((date, competition) -> competition.closeAssessment(date), dates);
    }

    public CompetitionBuilder withAssessorCount(Integer... assessorCounts) {
        return withArraySetFieldByReflection("assessorCount", assessorCounts);
    }
    public CompetitionBuilder withInnovationSector(InnovationSector... innovationSectors) {
        return withArray((innovationSector, competition) -> competition.setInnovationSector(innovationSector), innovationSectors);
    }

    public CompetitionBuilder withNonIfs(Boolean... nonIfs) {
        return withArraySetFieldByReflection("nonIfs", nonIfs);
    }

    public CompetitionBuilder withNonIfsUrl(String... nonIfsUrl) {
        return withArraySetFieldByReflection("nonIfsUrl", nonIfsUrl);
    }

    public CompetitionBuilder withCompetitionStatus(CompetitionStatus status) {
        ZonedDateTime now = ZonedDateTime.now();
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
    				.withFundersPanelDate(now.minusDays(1L));
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

    public CompetitionBuilder withAssessorFeedbackDate(ZonedDateTime... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(date), endDate);
    }

    /**
     * Convenience method to set dates with format dd/MM/yyyy
     *
     * @param endDate
     * @return
     */
    public CompetitionBuilder withAssessorFeedbackDate(String... endDate) {
        return withArray((date, competition) -> competition.setAssessorFeedbackDate(LocalDateTime.parse(date, DATE_FORMAT).atZone(ZoneId.systemDefault())), endDate);
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

    public CompetitionBuilder withMaxResearchRatio(Integer... ratios) { return withArray((ratio, competition) -> competition.setMaxResearchRatio(ratio), ratios); }

}
