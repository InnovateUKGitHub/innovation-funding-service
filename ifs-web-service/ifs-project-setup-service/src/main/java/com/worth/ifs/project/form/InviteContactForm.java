package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class InviteContactForm extends BaseBindingResultTarget {

	@NotNull(message = "{validation.invitecontactform.name.required}")
	private String name;

	private String email ;

	// for spring form binding
	public InviteContactForm() {
	}

	public Long getFinanceContact() {
		return financeContact;
	}
	
	public void setFinanceContact(Long financeContact) {
		this.financeContact = financeContact;
	}
	
	public Long getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(Long organisation) {
		this.organisation = organisation;
	}
}