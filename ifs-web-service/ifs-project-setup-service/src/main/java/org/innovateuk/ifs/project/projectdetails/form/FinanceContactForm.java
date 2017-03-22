package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.commons.validation.constraints.EmailRequiredIfOptionIs;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIfOptionIs;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form field model for the finance contact content
 */
@FieldRequiredIfOptionIs(required = "name", argument = "financeContact", predicate = -1L, message = "{validation.project.invite.name.required}")
@EmailRequiredIfOptionIs(required = "email", argument = "financeContact", predicate = -1L, regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.project.invite.email.required}", invalidMessage= "{validation.project.invite.email.invalid}")
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

	private String name;

	private String email;

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
