package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.competition.resource.CompetitionStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.competition.resource.CompetitionStatus.*;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ApplicationResource {
    private static final String ID_PATTERN = "#00000000";
    private static final int MAX_DURATION_IN_MONTHS_DIGITS = 2;
    public static final DecimalFormat formatter = new DecimalFormat(ID_PATTERN);

    private static final List<CompetitionStatus> PUBLISHED_ASSESSOR_FEEDBACK_COMPETITION_STATES = singletonList(PROJECT_SETUP);
    private static final List<CompetitionStatus> EDITABLE_ASSESSOR_FEEDBACK_COMPETITION_STATES = asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK);
    private static final List<CompetitionStatus> SUBMITABLE_COMPETITION_STATES = asList(OPEN);
    private static final List<Long> SUBMITTED_APPLICATION_STATES =
            simpleMap(asList(ApplicationStatusConstants.SUBMITTED, ApplicationStatusConstants.APPROVED, ApplicationStatusConstants.REJECTED), ApplicationStatusConstants::getId);

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDateTime submittedDate;

    @Digits(integer = MAX_DURATION_IN_MONTHS_DIGITS, fraction = 0, message="{validation.application.details.duration.in.months.max.digits}")
    private Long durationInMonths;

    private Long applicationStatus;
    private String applicationStatusName;
    private Long competition;
    private String competitionName;
    private Long assessorFeedbackFileEntry;
    private CompetitionStatus competitionStatus;
    private BigDecimal completion;
    private Boolean stateAidAgreed;
    private Boolean resubmission;
    private String previousApplicationNumber;
    private String previousApplicationTitle;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public String getFormattedId(){
        return formatter.format(id);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getApplicationDisplayName() {
        if(StringUtils.isNotEmpty(name)){
            return name;
        }else{
            return competitionName;
        }
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

    public Long getApplicationStatus() {
        return applicationStatus;
    }

    @JsonIgnore
    public void setApplicationStatusConstant(ApplicationStatusConstants applicationStatus) {
        this.applicationStatus = applicationStatus.getId();
        this.applicationStatusName = applicationStatus.getName();
    }

    public void setApplicationStatus(Long applicationStatus) {
        this.applicationStatus = applicationStatus;
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
        return ApplicationStatusConstants.OPEN.getId().equals(applicationStatus) || ApplicationStatusConstants.CREATED.getId().equals(applicationStatus);
    }
    @JsonIgnore
    public void enableViewMode(){
        setApplicationStatus(ApplicationStatusConstants.SUBMITTED.getId());
    }

    public Long getAssessorFeedbackFileEntry() {
        return assessorFeedbackFileEntry;
    }

    public void setAssessorFeedbackFileEntry(Long assessorFeedbackFileEntry) {
        this.assessorFeedbackFileEntry = assessorFeedbackFileEntry;
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
                .append(applicationStatus, that.applicationStatus)
                .append(competition, that.competition)
                .append(assessorFeedbackFileEntry, that.assessorFeedbackFileEntry)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(startDate)
                .append(durationInMonths)
                .append(applicationStatus)
                .append(competition)
                .append(assessorFeedbackFileEntry)
                .toHashCode();
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public String getApplicationStatusName() {
        return applicationStatusName;
    }

    public void setApplicationStatusName(String applicationStatusName) {
        this.applicationStatusName = applicationStatusName;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public void setCompetitionStatus(CompetitionStatus competitionStatus) {
        this.competitionStatus = competitionStatus;
    }

    public boolean hasPublishedAssessorFeedback() {
        return isInPublishedAssessorFeedbackCompetitionState() && getAssessorFeedbackFileEntry() != null;
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
    public boolean isSubmitable() {
        return isInSubmitableCompetitionState() && !hasBeenSubmitted();
    }

    @JsonIgnore
    public boolean hasBeenSubmitted() {
        return SUBMITTED_APPLICATION_STATES.contains(applicationStatus);
    }

    private boolean isInSubmitableCompetitionState() {
        return SUBMITABLE_COMPETITION_STATES.contains(competitionStatus);
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
}
