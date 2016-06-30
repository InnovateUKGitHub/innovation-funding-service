package com.worth.ifs.project.form;

import com.worth.ifs.project.viewmodel.ProjectDetailsAddressViewModelForm;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

public class BankDetailsForm extends ProjectDetailsAddressViewModelForm {
    @NotEmpty(message="Please enter a valid sort code")
    @Pattern(regexp = "\\d{8}", message = "Please enter a valid sort code")
    private String sortCode;

    @NotEmpty(message="Please enter a valid account number")
    @Pattern(regexp = "\\d{6}", message = "Please enter a valid account number")
    private String accountNumber;

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
