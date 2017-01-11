package org.innovateuk.ifs.project.form;

import org.innovateuk.ifs.project.form.validation.ValidateInviteForm;

import javax.validation.constraints.NotNull;

public class ProjectManagerForm extends ValidateInviteForm {
    @NotNull(message = "{validation.projectmanagerform.projectmanager.required}")
	private Long projectManager;

	public Long getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	public boolean inviteRequired() {
		return projectManager == -1L;
	}

}
