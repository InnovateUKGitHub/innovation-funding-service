package com.worth.ifs.project.form;

import com.worth.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.constraints.NotNull;
import java.util.List;

public class FinanceContactForm implements BindingResultTarget {

	@NotNull(message = "You need to select a Finance Contact before you can continue")
	private Long financeContact;

	private Long organisation;

	private List<ObjectError> objectErrors;

	private BindingResult bindingResult;

	// for spring form binding
	public FinanceContactForm() {
	}

	public Long getFinanceContact() {
		return financeContact;
	}
	
	public void setFinanceContact(Long financeContact) {
		this.financeContact = financeContact;
	}
	
	public Long getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(Long organisation) {
		this.organisation = organisation;
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