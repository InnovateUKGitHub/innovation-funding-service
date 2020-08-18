package org.innovateuk.ifs.registration.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class KnowledgeBaseCreateForm implements Serializable {

    private String name;

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
        if (rtoNumber != null) {
            return rtoNumber;
        }
        if (catapultNumber != null) {
            return catapultNumber;
        }
        if (universityNumber != null) {
            return universityNumber;
        }
        return null;
    }
}
