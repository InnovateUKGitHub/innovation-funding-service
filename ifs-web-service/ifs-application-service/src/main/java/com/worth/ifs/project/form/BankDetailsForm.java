package com.worth.ifs.project.form;

import com.worth.ifs.project.viewmodel.ProjectDetailsAddressViewModelForm;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class BankDetailsForm extends ProjectDetailsAddressViewModelForm {
    @NotEmpty(message="Please enter a valid sort code")
    private String sortCode;

    @NotEmpty(message="Please enter a valid sort code")
    private String accountNumber;

    @Override
    public BindingResult getBindingResult() {
        return null;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {

    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return null;
    }

    @Override
    public void setObjectErrors(List<ObjectError> errors) {

    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
