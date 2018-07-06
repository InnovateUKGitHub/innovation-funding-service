package org.innovateuk.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

public class ApplicationResource {
    private static final List<CompetitionStatus> PUBLISHED_ASSESSOR_FEEDBACK_COMPETITION_STATES = singletonList(PROJECT_SETUP);

    private static final List<CompetitionStatus> EDITABLE_ASSESSOR_FEEDBACK_COMPETITION_STATES = asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK);
    private static final List<CompetitionStatus> SUBMITTABLE_COMPETITION_STATES = singletonList(OPEN);

    private Long id;

    private String name;

    private LocalDate startDate;

    private ZonedDateTime submittedDate;

    private Long durationInMonths;

    private ApplicationState applicationState;
    private Long competition;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private BigDecimal completion;
    private Boolean stateAidAgreed;
    private Boolean resubmission;

    private String previousApplicationNumber;
    private String previousApplicationTitle;
    private ResearchCategoryResource researchCategory;
    private InnovationAreaResource innovationArea;

    private boolean noInnovationAreaApplicable;

    private IneligibleOutcomeResource ineligibleOutcome;

    private String leadOrganisationName;

    private boolean isInAssessmentReviewPanel;

    private boolean useNewApplicantMenu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    @JsonIgnore
    public String getApplicationStateDisplayName(){
        return applicationState.getDisplayName();
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public void setResubmission(Boolean resubmission) { this.resubmission = resubmission; }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public void setPreviousApplicationNumber(String previousApplicationNumber) { this.previousApplicationNumber = previousApplicationNumber; }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public void setPreviousApplicationTitle(String previousApplicationTitle) { this.previousApplicationTitle = previousApplicationTitle; }

    @JsonIgnore
    public boolean isOpen(){
        return applicationState == ApplicationState.OPEN || applicationState == ApplicationState.CREATED;
    }

    @JsonIgnore
    public boolean isApproved(){
        return applicationState == ApplicationState.APPROVED;
    }

    public IneligibleOutcomeResource getIneligibleOutcome() {
        return ineligibleOutcome;
    }

    public void setIneligibleOutcome(final IneligibleOutcomeResource ineligibleOutcome) {
        this.ineligibleOutcome = ineligibleOutcome;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public void setLeadOrganisationName(String leadOrganisationName) {
        this.leadOrganisationName = leadOrganisationName;
    }


    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public ZonedDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(ZonedDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    @JsonIgnore
    public boolean isInPublishedAssessorFeedbackCompetitionState() {
        return PUBLISHED_ASSESSOR_FEEDBACK_COMPETITION_STATES.contains(competitionStatus);
    }

    @JsonIgnore
    public boolean isInEditableAssessorFeedbackCompetitionState() {
        return EDITABLE_ASSESSOR_FEEDBACK_COMPETITION_STATES.contains(competitionStatus);
    }

    @JsonIgnore
    public boolean isSubmittable() {
        return isInSubmittableCompetitionState() && !isSubmitted();
    }

    @JsonIgnore
    public boolean isSubmitted() {
        return ApplicationState.submittedAndFinishedStates.contains(applicationState);
    }

    @JsonIgnore
    public boolean isWithdrawn() { return ApplicationState.WITHDRAWN.equals(applicationState); }

    private boolean isInSubmittableCompetitionState() {
        return SUBMITTABLE_COMPETITION_STATES.contains(competitionStatus);
    }

    public BigDecimal getCompletion() {
        return completion;
    }

    public void setCompletion(final BigDecimal completion) {
        this.completion = completion;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    public ResearchCategoryResource getResearchCategory() {
        return researchCategory;
    }

    public void setResearchCategory(ResearchCategoryResource researchCategory) {
        this.researchCategory = researchCategory;
    }

    public InnovationAreaResource getInnovationArea() {
        return innovationArea;
    }

    public void setInnovationArea(InnovationAreaResource innovationArea) {
        this.innovationArea = innovationArea;
    }

    public boolean getNoInnovationAreaApplicable() {
        return noInnovationAreaApplicable;
    }

    public void setNoInnovationAreaApplicable(boolean noInnovationAreaApplicable) {
        this.noInnovationAreaApplicable = noInnovationAreaApplicable;
    }

    public boolean isInAssessmentReviewPanel() {
        return isInAssessmentReviewPanel;
    }

    public void setInAssessmentReviewPanel(boolean inAssessmentReviewPanel) {
        this.isInAssessmentReviewPanel = inAssessmentReviewPanel;
    }

    public boolean isUseNewApplicantMenu() {
        return useNewApplicantMenu;
    }

    public void setUseNewApplicantMenu(final boolean useNewApplicantMenu) {
        this.useNewApplicantMenu = useNewApplicantMenu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationResource that = (ApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(startDate, that.startDate)
                .append(durationInMonths, that.durationInMonths)
                .append(applicationState, that.applicationState)
                .append(ineligibleOutcome, that.ineligibleOutcome)
                .append(competition, that.competition)
                .append(leadOrganisationName, that.leadOrganisationName)
                .append(isInAssessmentReviewPanel, that.isInAssessmentReviewPanel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(startDate)
                .append(durationInMonths)
                .append(applicationState)
                .append(ineligibleOutcome)
                .append(competition)
                .append(leadOrganisationName)
                .append(isInAssessmentReviewPanel)
                .toHashCode();
    }
}