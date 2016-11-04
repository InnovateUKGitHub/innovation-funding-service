package com.worth.ifs.project.form;

import com.worth.ifs.commons.validation.ValidationConstants;
import com.worth.ifs.commons.validation.constraints.FieldRequiredIfOptionIs;
import com.worth.ifs.controller.BaseBindingResultTarget;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@FieldRequiredIfOptionIs(required = "name", argument = "projectManager", predicate = -1L, message = "{validation.project.invite.name.required}")
@FieldRequiredIfOptionIs(required = "email", argument = "projectManager", predicate = -1L, message = "{validation.project.invite.email.required}")
public class ProjectManagerForm  extends BaseBindingResultTarget {
    @NotNull(message = "{validation.projectmanagerform.projectmanager.required}")
	private Long projectManager;

	private String name;

	@Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message= "{validation.project.invite.email.invalid}")
	private String email;

	public Long getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
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
