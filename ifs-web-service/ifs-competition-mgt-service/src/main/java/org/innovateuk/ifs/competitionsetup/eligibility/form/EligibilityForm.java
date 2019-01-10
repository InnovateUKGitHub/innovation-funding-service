package org.innovateuk.ifs.competitionsetup.eligibility.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

/**
 * Form for the eligibility competition setup section.
 */
@FieldRequiredIf(required = "researchCategoryId", argument = "researchCategoriesApplicable", predicate = true, message = "{validation.eligibilityform.researchcategoryid.required}")
@FieldRequiredIf(required = "overrideFundingRules", argument = "researchCategoriesApplicable", predicate = true, message = "{validation.eligibilityform.overrideFundingRules.required}")
@FieldRequiredIf(required = "fundingLevelPercentage", argument = "researchCategoriesApplicable", predicate = false, message = "{validation.eligibilityform.fundingLevel.required}")
@FieldRequiredIf(required = "fundingLevelPercentageOverride", argument = "overrideFundingRules", predicate = true, message = "{validation.eligibilityform.fundingLevel.required}")
public class EligibilityForm extends CompetitionSetupForm {

    @NotBlank(message = "{validation.eligibilityform.multiplestream.required}")
    private String multipleStream;

    @Size(max = 255, message = "{validation.eligibilityform.streamname.length.max}")
    private String streamName;

    @NotNull(message = "{validation.eligibilityform.researchCategoriesApplicable.required}")
    private Boolean researchCategoriesApplicable;

    private Set<Long> researchCategoryId;

    @NotBlank(message = "{validation.eligibilityform.singleorcollaborative.required}")
    private String singleOrCollaborative;

    @NotEmpty(message = "{validation.eligibilityform.leadApplicantTypes.required}")
    private List<Long> leadApplicantTypes;

    private Boolean overrideFundingRules;

    private Integer fundingLevelPercentage;

    private Integer fundingLevelPercentageOverride;

    @NotNull(message = "{validation.eligibilityform.researchparticipationamountId.required}")
    private int researchParticipationAmountId = ResearchParticipationAmount.NONE.getId();

    @NotBlank(message = "{validation.eligibilityform.resubmission.required}")
    private String resubmission;

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

    public Boolean getResearchCategoriesApplicable() {
        return researchCategoriesApplicable;
    }

    public void setResearchCategoriesApplicable(final Boolean researchCategoriesApplicable) {
        this.researchCategoriesApplicable = researchCategoriesApplicable;
    }

    public Set<Long> getResearchCategoryId() {
        return researchCategoryId;
    }

    public void setResearchCategoryId(Set<Long> researchCategoryId) {
        this.researchCategoryId = researchCategoryId;
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

    public Boolean getOverrideFundingRules() {
        return overrideFundingRules;
    }

    public void setOverrideFundingRules(Boolean overrideFundingRules) {
        this.overrideFundingRules = overrideFundingRules;
    }

    public Integer getConfiguredFundingLevelPercentage() {
        if (Boolean.TRUE.equals(getOverrideFundingRules())) {
            return getFundingLevelPercentageOverride();
        }
        return getFundingLevelPercentage();
    }

    public Integer getFundingLevelPercentage() {
        return fundingLevelPercentage;
    }

    public void setFundingLevelPercentage(Integer fundingLevelPercentage) {
        this.fundingLevelPercentage = fundingLevelPercentage;
    }

    public Integer getFundingLevelPercentageOverride() {
        return fundingLevelPercentageOverride;
    }

    public void setFundingLevelPercentageOverride(Integer fundingLevelPercentageOverride) {
        this.fundingLevelPercentageOverride = fundingLevelPercentageOverride;
    }

    public int getResearchParticipationAmountId() {
        return researchParticipationAmountId;
    }

    public void setResearchParticipationAmountId(int researchParticipationAmountId) {
        this.researchParticipationAmountId = researchParticipationAmountId;
    }

    public boolean includesResearchCategory(Long id) {
        return researchCategoryId != null && researchCategoryId.contains(id);
    }

    public boolean includesLeadApplicantType(Long id) {
        return leadApplicantTypes != null && leadApplicantTypes.contains(id);
    }
}
