package org.innovateuk.ifs.management.competition.setup.projecteligibility.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.funding.form.enumerable.ResearchParticipationAmount;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * Form for the eligibility competition setup section.
 */
@FieldRequiredIf(required = "leadApplicantTypes", argument = "ktpCompetition", predicate = false, message = "{validation.eligibilityform.leadApplicantTypes.required}")
public class ProjectEligibilityForm extends CompetitionSetupForm {

    @NotBlank(message = "{validation.eligibilityform.multiplestream.required}")
    private String multipleStream;

    @Size(max = 255, message = "{validation.eligibilityform.streamname.length.max}")
    private String streamName;

    @NotBlank(message = "{validation.eligibilityform.singleorcollaborative.required}")
    private String singleOrCollaborative;

    private List<Long> leadApplicantTypes;

    @NotNull(message = "{validation.eligibilityform.researchparticipationamountId.required}")
    private int researchParticipationAmountId = ResearchParticipationAmount.NONE.getId();

    @NotBlank(message = "{validation.eligibilityform.resubmission.required}")
    private String resubmission;

    private boolean ktpCompetition;

    public String getMultipleStream() {
        return multipleStream;
    }

    public void setMultipleStream(String multipleStream) {
        this.multipleStream = multipleStream;
    }

    public String getResubmission() {
        return resubmission;
    }

    public void setResubmission(String resubmission) {
        this.resubmission = resubmission;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getSingleOrCollaborative() {
        return singleOrCollaborative;
    }

    public void setSingleOrCollaborative(String singleOrCollaborative) {
        this.singleOrCollaborative = singleOrCollaborative;
    }

    public List<Long> getLeadApplicantTypes() {
        return leadApplicantTypes;
    }

    public void setLeadApplicantTypes(List<Long> leadApplicantTypes) {
        this.leadApplicantTypes = leadApplicantTypes;
    }

    public int getResearchParticipationAmountId() {
        return researchParticipationAmountId;
    }

    public void setResearchParticipationAmountId(int researchParticipationAmountId) {
        this.researchParticipationAmountId = researchParticipationAmountId;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public void setKtpCompetition(boolean ktpCompetition) {
        this.ktpCompetition = ktpCompetition;
    }

    public boolean includesLeadApplicantType(Long id) {
        return leadApplicantTypes != null && leadApplicantTypes.contains(id);
    }

    public static class FundingLevelMaxPredicateProvider implements BiPredicateProvider<Integer, Boolean> {
        public BiPredicate<Integer, Boolean> predicate() {
            return (fundingLevelPercentageOverride, overrideFundingRules) -> isFundingLevelLessThan(fundingLevelPercentageOverride, overrideFundingRules);
        }

        private boolean isFundingLevelLessThan(Integer fundingLevelPercentageOverride, Boolean overrideFundingRules) {
            if (overrideFundingRules) {
                return fundingLevelPercentageOverride <= FundingLevel.HUNDRED.getPercentage();
            }
            return true;
        }
    }

    public static class FundingLevelMinPredicateProvider implements BiPredicateProvider<Integer, Boolean> {
        public BiPredicate<Integer, Boolean> predicate() {
            return (fundingLevelPercentageOverride, overrideFundingRules) -> isFundingLevelLessThan(fundingLevelPercentageOverride, overrideFundingRules);
        }

        private boolean isFundingLevelLessThan(Integer fundingLevelPercentageOverride, Boolean overrideFundingRules) {
            if (overrideFundingRules) {
                return fundingLevelPercentageOverride > 0;
            }
            return true;
        }
    }
}
