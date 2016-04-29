package com.worth.ifs.profile;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

public class OrganisationDetailsForm {
	@NotEmpty(message = "Please enter an organisation name")
	 @Size.List ({
	        @Size(min=1, message="Organisation name should have at least 1 characters"),
	        @Size(max=70, message="Organisation name cannot have more than 70 characters"),
	    })
    private String name;

    @NotEmpty(message = "Please enter a companies house number")
    @Size.List ({
        @Size(min=5, message="Input for your companies house number has a minimum length of 8 characters"),
        @Size(max=20, message="Input for your companies house number has a maximum length of 20 characters")
    })
    @Pattern(regexp = "([0-9\\ +-])+",  message= "Please enter a valid companies house number")
    private String companyHouseNumber;
    
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    @NotBlank
    private String town;
    private String county;
    @NotBlank
    @Length(max = 9)
    private String postcode;
    
    public String getName() {
		return name;
	}
    public void setName(String name) {
		this.name = name;
	}
    public String getCompanyHouseNumber() {
		return companyHouseNumber;
	}
    public void setCompanyHouseNumber(String companyHouseNumber) {
		this.companyHouseNumber = companyHouseNumber;
	}
    public String getAddressLine1() {
		return addressLine1;
	}
    public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
    public String getAddressLine2() {
		return addressLine2;
	}
    public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
    public String getAddressLine3() {
		return addressLine3;
	}
    public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}
    public String getCounty() {
		return county;
	}
    public void setCounty(String county) {
		this.county = county;
	}
    public String getPostcode() {
		return postcode;
	}
    public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
    public String getTown() {
		return town;
	}
    public void setTown(String town) {
		this.town = town;
	}
}
