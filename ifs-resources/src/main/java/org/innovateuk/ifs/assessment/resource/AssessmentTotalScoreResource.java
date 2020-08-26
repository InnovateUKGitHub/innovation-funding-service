package org.innovateuk.ifs.assessment.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource class holding scoring related information about an Assessment.
 */
public class AssessmentTotalScoreResource {

    private Integer totalScoreGiven;
    private Integer totalScorePossible;

    public AssessmentTotalScoreResource() {
    }

    public AssessmentTotalScoreResource(Integer totalScoreGiven, Integer totalScorePossible) {
        this.totalScoreGiven = totalScoreGiven;
        this.totalScorePossible = totalScorePossible;
    }

    public Integer getTotalScoreGiven() {
        return totalScoreGiven;
    }

    public void setTotalScoreGiven(int totalScoreGiven) {
        this.totalScoreGiven = totalScoreGiven;
    }

    public Integer getTotalScorePossible() {
        return totalScorePossible;
    }

    public void setTotalScorePossible(int totalScorePossible) {
        this.totalScorePossible = totalScorePossible;
    }

    @JsonIgnore
    public Integer getTotalScorePercentage() {
        if (totalScoreGiven == null || totalScoreGiven == 0) {
            return 0;
        }
        return totalScorePossible == 0 ? 0 : Math.round(totalScoreGiven * 100.0f / totalScorePossible);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessmentTotalScoreResource that = (AssessmentTotalScoreResource) o;

        return new EqualsBuilder()
                .append(totalScoreGiven, that.totalScoreGiven)
                .append(totalScorePossible, that.totalScorePossible)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalScoreGiven)
                .append(totalScorePossible)
                .toHashCode();
    }
}
