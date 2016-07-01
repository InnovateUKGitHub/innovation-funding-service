package com.worth.ifs.project.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

public class ProjectManagerForm  extends BaseBindingResultTarget {

    @NotNull(message = "You need to select a Project Manager before you can continue")
	private Long projectManager;

	public Long getProjectManager() {
		return projectManager;
	}
	
	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}
}
