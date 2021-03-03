package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form field model for the finance contact content
 */
public class FinanceContactForm extends BaseBindingResultTarget {

	@NotNull(message = "{validation.financecontactform.financecontact.required}")
	private Long financeContact;

	private Long organisation;

	public Long getFinanceContact() {
		return financeContact;
	}
	
	public Long getOrganisation() {
		return organisation;
	}

	public void setFinanceContact(Long financeContact) {
		this.financeContact = financeContact;
	}

	public void setOrganisation(Long organisation) {
		this.organisation = organisation;
	}
}
