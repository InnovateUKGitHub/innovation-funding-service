package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

public class FinanceContactForm  extends BaseBindingResultTarget {

	//@NotNull(message = "{validation.financecontactform.financecontact.required}")
	private Long financeContact;

	private Long organisation;

	//@NotEmpty
	private String name;
	//@NotEmpty
	//@Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX)
	private String email;

	// for spring form binding
	public FinanceContactForm() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}