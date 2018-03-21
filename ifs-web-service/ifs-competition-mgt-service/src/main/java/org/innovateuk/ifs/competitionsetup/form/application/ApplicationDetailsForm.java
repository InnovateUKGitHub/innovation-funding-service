package org.innovateuk.ifs.competitionsetup.form.application;

import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;
import java.util.function.BiPredicate;

@FieldComparison(
        firstField = "maxProjectDuration",
        secondField = "minProjectDuration",
        message = "{competition.setup.applicationdetails.max.projectduration.beneathmin}",
        predicate = ApplicationDetailsForm.MaxBeneathMinPredicateProvider.class)
@FieldComparison(
        firstField = "minProjectDuration",
        secondField = "maxProjectDuration",
        message = "{competition.setup.applicationdetails.min.projectduration.exceedsmax}",
        predicate = ApplicationDetailsForm.MinExceedsMaxPredicateProvider.class)
public class ApplicationDetailsForm extends CompetitionSetupForm {

    @NotNull
    private Integer minProjectDuration;

    @NotNull
    private Integer maxProjectDuration;

    private boolean useResubmissionQuestion;

    public Integer getMinProjectDuration() {
        return minProjectDuration;
    }

    public void setMinProjectDuration(Integer minProjectDuration) {
        this.minProjectDuration = minProjectDuration;
    }

    public Integer getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public void setMaxProjectDuration(Integer maxProjectDuration) {
        this.maxProjectDuration = maxProjectDuration;
    }

    public boolean isUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public static class MaxBeneathMinPredicateProvider implements BiPredicateProvider<Integer, Integer> {
        public MaxBeneathMinPredicateProvider() { }

        public BiPredicate<Integer, Integer> predicate() {
            return (max, min) -> max >= min;
        }
    }

    public static class MinExceedsMaxPredicateProvider implements BiPredicateProvider<Integer, Integer> {
        public MinExceedsMaxPredicateProvider() { }

        public BiPredicate<Integer, Integer> predicate() {
            return (min, max) -> min <= max;
        }
    }
}
