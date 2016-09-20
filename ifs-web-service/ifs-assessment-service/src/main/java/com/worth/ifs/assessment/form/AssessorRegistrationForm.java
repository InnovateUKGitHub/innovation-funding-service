package com.worth.ifs.assessment.form;

import com.worth.ifs.form.AddressForm;
import com.worth.ifs.validator.constraints.FieldMatch;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Form field model for the Assessor Registration
 */
@FieldMatch(first = "password", second = "retypedPassword", message = "Passwords must match")
public class AssessorRegistrationForm {

    @NotEmpty(message = "{validation.standard.title.selectionrequired}")
    @Size(max = 5, message = "{validation.standard.title.length.max}")
    @Pattern(regexp = "^(Mr|Miss|Mrs|Ms|Dr)$", message = "{validation.standard.title.format}")
    private String title;

    @NotEmpty(message = "{validation.standard.firstname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.firstname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.firstname.length.min}"),
            @Size(max=70, message="{validation.standard.firstname.length.max}"),
    })
    private String firstName;

    @NotEmpty(message = "{validation.standard.lastname.required}")
    @Pattern(regexp = "[\\p{L} -]*", message = "{validation.standard.lastname.required}")
    @Size.List ({
            @Size(min=2, message="{validation.standard.lastname.length.min}"),
            @Size(max=70, message="{validation.standard.lastname.length.max}"),
    })
    private String lastName;

    @NotEmpty(message = "{validation.standard.password.required}")
    @Size.List ({
            @Size(min=10, message="{validation.standard.password.length.min}"),
            @Size(max=30, message="{validation.standard.password.length.max}"),
    })
    private String password;

    @NotEmpty(message = "{validation.standard.retypedpassword.required}")
    @Size.List ({
            @Size(min=10, message="{validation.standard.password.length.min}"),
            @Size(max=30, message="{validation.standard.password.length.max}"),
    })
    private String retypedPassword;

    //private Gender gender;
    //private EthnicityResource ethnicity;
    //private Disability disability;

    @Valid
    private AddressForm addressForm = new AddressForm();

    @NotEmpty(message = "{validation.standard.phonenumber.required}")
    @Size.List ({
            @Size(min=8, message="{validation.standard.phonenumber.length.min}"),
            @Size(max=20, message="{validation.standard.phonenumber.length.max}")
    })
    @Pattern(regexp = "([0-9\\ +-])+",  message= "{validation.standard.phonenumber.format}")
    private String phoneNumber;




    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }
    private boolean useSearchResultAddress = false;

    public boolean isUseSearchResultAddress() {
        return useSearchResultAddress;
    }

    public void setUseSearchResultAddress(boolean useSearchResultAddress) {
        this.useSearchResultAddress = useSearchResultAddress;
    }
}
