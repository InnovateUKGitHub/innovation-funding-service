package org.innovateuk.ifs.assessment.feedback.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of model attributes for the Application Detail view shown as part of the assessment journey.
 */
public class AssessmentFeedbackApplicationDetailsViewModel {

    private long applicationId;
    private String applicationName;
    private LocalDate applicationStartDate;
    private long applicationDurationInMonths;
    private long daysLeft;
    private long daysLeftPercentage;
    private String questionShortName;

    public AssessmentFeedbackApplicationDetailsViewModel(long applicationId,
                                                         String applicationName,
                                                         LocalDate applicationStartDate,
                                                         long applicationDurationInMonths,
                                                         long daysLeft,
                                                         long daysLeftPercentage,
                                                         String questionShortName) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.applicationStartDate = applicationStartDate;
        this.applicationDurationInMonths = applicationDurationInMonths;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.questionShortName = questionShortName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public long getApplicationDurationInMonths() {
        return applicationDurationInMonths;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public String getQuestionShortName() {
        return questionShortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentFeedbackApplicationDetailsViewModel that = (AssessmentFeedbackApplicationDetailsViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(applicationDurationInMonths, that.applicationDurationInMonths)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(applicationName, that.applicationName)
                .append(applicationStartDate, that.applicationStartDate)
                .append(questionShortName, that.questionShortName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(applicationStartDate)
                .append(applicationDurationInMonths)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(questionShortName)
                .toHashCode();
    }
}
