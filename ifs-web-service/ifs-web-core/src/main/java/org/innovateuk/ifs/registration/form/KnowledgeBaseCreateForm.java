package org.innovateuk.ifs.registration.form;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

public class KnowledgeBaseCreateForm {

    private String name;

    private Long organisationType;

    private String identification;

    @Valid
    private AddressForm addressForm = new AddressForm();

    public KnowledgeBaseCreateForm() {
    }

    public KnowledgeBaseCreateForm(String name, Long organisationType, String identification, AddressForm addressForm) {
        this.name = name;
        this.organisationType = organisationType;
        this.identification = identification;
        this.addressForm = addressForm;
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

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }
}
