package com.worth.ifs.competitionsetup.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Form for the eligibility competition setup section.
 */
public class EligibilityForm extends CompetitionSetupForm {
	@NotEmpty(message = "{validation.eligibilityform.researchcategoryid.required}")
	private Set<Long> researchCategoryId;
	@NotBlank(message = "{validation.eligibilityform.singleorcollaborative.required}")
	private String singleOrCollaborative;
	@NotBlank(message = "{validation.eligibilityform.leadapplicanttype.required}")
	private String leadApplicantType;
	@NotNull(message = "{validation.eligibilityform.researchparticipationamountId.required}")
	private Integer researchParticipationAmountId;
	@NotBlank(message = "{validation.eligibilityform.resubmission.required}")
	private String resubmission;

    public String getResubmission() {
        return resubmission;
    }

    public void setResubmission(String resubmission) {
        this.resubmission = resubmission;
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
	public String getLeadApplicantType() {
		return leadApplicantType;
	}
	public void setLeadApplicantType(String leadApplicantType) {
		this.leadApplicantType = leadApplicantType;
	}
	public Integer getResearchParticipationAmountId() {
		return researchParticipationAmountId;
	}
	public void setResearchParticipationAmountId(Integer researchParticipationAmountId) {
		this.researchParticipationAmountId = researchParticipationAmountId;
	}
	
	public boolean includesResearchCategory(Long id) {
		if(this.researchCategoryId != null) {
			for(Long cat: this.researchCategoryId) {
				if(cat.equals(id)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
