package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class InviteContactForm extends BaseBindingResultTarget {

	//TODO: POPULATE the list of invited contacts

	@NotNull(message = "{validation.invitecontactform.name.required}")
	private String name;

	@NotNull(message = "{validation.invitecontactform.email.required}")
	@Email(message = "{validation.standard.email.format}")
	@Size(max = 256, message = "{validation.standard.email.length.max}")
	private String email ;

	private Long organisation;

	private List<String> invitedContacts;

	// for spring form binding
	public InviteContactForm() {
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

	public Long getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(Long organisation) {
		this.organisation = organisation;
	}

	public List<String> getInvitedContacts() {return invitedContacts;}

	public void setInvitedContacts (List <String> invitedContacts) {
		this.invitedContacts = invitedContacts;
	}


}