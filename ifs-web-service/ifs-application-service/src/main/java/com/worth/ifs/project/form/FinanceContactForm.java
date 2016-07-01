package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class FinanceContactForm  extends BaseBindingResultTarget {

	@NotNull(message = "You need to select a Finance Contact before you can continue")
	private Long financeContact;

	private Long organisation;

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
}