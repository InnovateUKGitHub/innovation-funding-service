package com.worth.ifs.controller.form.competitionsetup;

/**
 * Form for the eligibility competition setup section.
 */
public class EligibilityForm extends CompetitionSetupForm {

	private String multipleStream;
	private Long innovationSectorCategoryId;
	private String singleOrCollaborative;
	private String leadApplicantType;
	private Long researchParticipation;
	private String resubmissionsAllowed;
	
	public String getMultipleStream() {
		return multipleStream;
	}
	public void setMultipleStream(String multipleStream) {
		this.multipleStream = multipleStream;
	}
	public Long getInnovationSectorCategoryId() {
		return innovationSectorCategoryId;
	}
	public void setInnovationSectorCategoryId(Long innovationSectorCategoryId) {
		this.innovationSectorCategoryId = innovationSectorCategoryId;
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
	public Long getResearchParticipation() {
		return researchParticipation;
	}
	public void setResearchParticipation(Long researchParticipation) {
		this.researchParticipation = researchParticipation;
	}
	public String getResubmissionsAllowed() {
		return resubmissionsAllowed;
	}
	public void setResubmissionsAllowed(String resubmissionsAllowed) {
		this.resubmissionsAllowed = resubmissionsAllowed;
	}
	
	
}
