package org.innovateuk.ifs.project.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.hibernate.validator.constraints.Email;
import org.innovateuk.ifs.project.form.validation.ProjectManagerInviteChecks;
import org.innovateuk.ifs.project.form.validation.ProjectManagerInviteSequenceProvider;

import javax.validation.constraints.NotNull;

@GroupSequenceProvider(ProjectManagerInviteSequenceProvider.class)
public class ProjectManagerForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.projectmanagerform.projectmanager.required}")
	private Long projectManager;

	@NotNull(groups = ProjectManagerInviteChecks.class, message = "{validation.project.invite.name.required}")
	@NotBlank(groups = ProjectManagerInviteChecks.class, message = "{validation.project.invite.name.required}")
	private String name;

	@NotNull(groups = ProjectManagerInviteChecks.class, message = "{validation.project.invite.email.required}")
	@NotBlank(groups = ProjectManagerInviteChecks.class, message = "{validation.project.invite.email.required}")
	@Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message= "{validation.project.invite.email.invalid}", groups = ProjectManagerInviteChecks.class)
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
