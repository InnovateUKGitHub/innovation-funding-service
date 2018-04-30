package org.innovateuk.ifs.competitionsetup.application.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldComparison;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.function.BiPredicate;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DIGITS;

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

    @NotNull(message = "{validation.field.must.not.be.blank}")
    @DecimalMin(value = "1", message = "{competition.setup.applicationdetails.projectduration.min}")
    @DecimalMax(value = "60", message = "{competition.setup.applicationdetails.projectduration.max}")
    @Digits(integer = MAX_DIGITS, fraction = 0, message = "{validation.standard.integer.non.decimal.format}")
    private BigDecimal minProjectDuration;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    @DecimalMin(value = "1", message = "{competition.setup.applicationdetails.projectduration.min}")
    @DecimalMax(value = "60", message = "{competition.setup.applicationdetails.projectduration.max}")
    @Digits(integer = MAX_DIGITS, fraction = 0, message = "{validation.standard.integer.non.decimal.format}")
    private BigDecimal maxProjectDuration;

    @NotNull(message = "{validation.application.must.indicate.resubmission.or.not}")
    private Boolean useResubmissionQuestion;

    public BigDecimal getMinProjectDuration() {
        return minProjectDuration;
    }

    public void setMinProjectDuration(BigDecimal minProjectDuration) {
        this.minProjectDuration = minProjectDuration;
    }

    public BigDecimal getMaxProjectDuration() {
        return maxProjectDuration;
    }

    public void setMaxProjectDuration(BigDecimal maxProjectDuration) {
        this.maxProjectDuration = maxProjectDuration;
    }

    public Boolean getUseResubmissionQuestion() {
        return useResubmissionQuestion;
    }

    public void setUseResubmissionQuestion(Boolean useResubmissionQuestion) {
        this.useResubmissionQuestion = useResubmissionQuestion;
    }

    public static class MaxBeneathMinPredicateProvider implements BiPredicateProvider<BigDecimal, BigDecimal> {
        public MaxBeneathMinPredicateProvider() { }

        public BiPredicate<BigDecimal, BigDecimal> predicate() {
            return (max, min) -> max.compareTo(min) >= 0;
        }
    }

    public static class MinExceedsMaxPredicateProvider implements BiPredicateProvider<BigDecimal, BigDecimal> {
        public MinExceedsMaxPredicateProvider() { }

        public BiPredicate<BigDecimal, BigDecimal> predicate() {
            return (min, max) -> min.compareTo(max) <= 0;
        }
    }
}
