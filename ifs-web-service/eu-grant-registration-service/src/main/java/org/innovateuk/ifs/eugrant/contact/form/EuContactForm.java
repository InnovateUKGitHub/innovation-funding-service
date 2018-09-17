package org.innovateuk.ifs.eugrant.contact.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static org.innovateuk.ifs.commons.validation.PhoneNumberValidator.VALID_PHONE_NUMBER;

public class EuContactForm extends BaseBindingResultTarget {

    @NotBlank(message = "{validation.eugrant.fullname.required}")
    @Pattern(regexp = "[\\p{L} \\-']*", message = "{validation.eugrant.fullname.invalid}")
    @Size.List ({
            @Size(min=2, message="{validation.eugrant.fullname.length.min}"),
            @Size(max=70, message="{validation.eugrant.fullname.length.max}"),
    })
    private String name;

    @NotBlank(message = "{validation.eugrant.jobtitle.required}")
    private String jobTitle;

    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message = "{validation.eugrant.email.required}")
    private String email;

    @NotBlank(message = "{validation.eugrant.telephone.required}")
    @Pattern(regexp = VALID_PHONE_NUMBER,  message= "{validation.eugrant.telephone.format}")
    private String telephone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuContactForm that = (EuContactForm) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(jobTitle, that.jobTitle)
                .append(email, that.email)
                .append(telephone, that.telephone)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(jobTitle)
                .append(email)
                .append(telephone)
                .toHashCode();
    }
}
