package com.worth.ifs.controller.form.competitionsetup;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form for the eligibility competition setup section.
 */
public class EligibilityForm extends CompetitionSetupForm {
	@NotNull(message = "Please select a stream option")
	private String multipleStream;
	@NotEmpty(message = "Please select at least one research category")
	private Long[] researchCategoryId;
	@NotNull(message = "Please select a competition executive")
	private String singleOrCollaborative;
	@NotNull(message = "Please select a lead applicant type")
	private String leadApplicantType;
	@NotNull(message = "Please select a research participation amount")
	private Integer researchParticipationAmountId;
	
	public String getMultipleStream() {
		return multipleStream;
	}
	public void setMultipleStream(String multipleStream) {
		this.multipleStream = multipleStream;
	}
	public Long[] getResearchCategoryId() {
		return researchCategoryId;
	}
	public void setResearchCategoryId(Long[] researchCategoryId) {
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
