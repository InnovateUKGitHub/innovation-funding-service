package org.innovateuk.ifs.registration.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class KnowledgeBaseCreateForm implements Serializable {

    @NotEmpty(message = "{validation.standard.knowledgebasename.required}")
    private String name;

    @NotNull(message = "{validation.standard.knowledgebasetype.required}")
    private Long organisationType;

    private String rtoNumber;

    private String catapultNumber;

    private String universityNumber;

    @Valid
    private AddressForm addressForm = new AddressForm();

    public KnowledgeBaseCreateForm() {
    }

    public KnowledgeBaseCreateForm(String name, Long organisationType, String rtoNumber, String catapultNumber, String universityNumber, AddressForm addressForm) {
        this.name = name;
        this.organisationType = organisationType;
        this.rtoNumber = rtoNumber;
        this.catapultNumber = catapultNumber;
        this.universityNumber = universityNumber;
        this.addressForm = addressForm;
    }

    public String getRtoNumber() {
        return rtoNumber;
    }

    public String getCatapultNumber() {
        return catapultNumber;
    }

    public String getUniversityNumber() {
        return universityNumber;
    }

    public void setRtoNumber(String rtoNumber) {
        this.rtoNumber = rtoNumber;
    }

    public void setCatapultNumber(String catapultNumber) {
        this.catapultNumber = catapultNumber;
    }

    public void setUniversityNumber(String universityNumber) {
        this.universityNumber = universityNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(Long organisationType) {
        this.organisationType = organisationType;
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    @JsonIgnore
    public String getIdentification() {
        if (!rtoNumber.isEmpty()) {
            return rtoNumber;
        }
        if (!catapultNumber.isEmpty()) {
            return catapultNumber;
        }
        if (!universityNumber.isEmpty()) {
            return universityNumber;
        }
        return null;
    }
}
