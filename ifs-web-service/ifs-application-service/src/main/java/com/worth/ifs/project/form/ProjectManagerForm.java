package com.worth.ifs.project.form;

import com.worth.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ProjectManagerForm implements BindingResultTarget {

    @NotNull(message = "You need to select a Project Manager before you can continue")
	private Long projectManager;

    private List<ObjectError> objectErrors;

    private BindingResult bindingResult;
	
	public Long getProjectManager() {
		return projectManager;
	}
	
	public void setProjectManager(Long projectManager) {
		this.projectManager = projectManager;
	}

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    @Override
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
