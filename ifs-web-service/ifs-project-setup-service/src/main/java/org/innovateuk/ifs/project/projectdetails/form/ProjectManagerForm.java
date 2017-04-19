package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.commons.validation.constraints.EmailRequiredIfOptionIs;
import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIfOptionIs;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form field model for the Project Manager content
 */
@FieldRequiredIfOptionIs(required = "name", argument = "projectManager", predicate = -1L, message = "{validation.project.invite.name.required}")
@EmailRequiredIfOptionIs(required = "inviteEmail", argument = "projectManager", predicate = -1L, regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.project.invite.email.required}", invalidMessage= "{validation.project.invite.email.invalid}")
public class ProjectManagerForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.projectmanagerform.projectmanager.required}")
	private Long projectManager;

	public Long getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}

	private String name;

	private String inviteEmail;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInviteEmail() {
		return inviteEmail;
	}

	public void setInviteEmail(String inviteEmail) {
		this.inviteEmail = inviteEmail;
	}

}
