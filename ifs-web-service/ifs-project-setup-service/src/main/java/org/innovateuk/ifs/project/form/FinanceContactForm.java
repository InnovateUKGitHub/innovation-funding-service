package org.innovateuk.ifs.project.form;

import org.innovateuk.ifs.project.form.validation.ValidateInviteForm;

import javax.validation.constraints.NotNull;

public class FinanceContactForm extends ValidateInviteForm {

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

	@Override
	public boolean inviteRequired() {
		return financeContact == -1L;
	}

}
