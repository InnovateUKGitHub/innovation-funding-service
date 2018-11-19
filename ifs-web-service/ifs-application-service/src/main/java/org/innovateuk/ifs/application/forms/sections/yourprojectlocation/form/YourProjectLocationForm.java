package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

/**
 * Form used to capture project location information
 */
public class YourProjectLocationForm {

    private String postcode;

    public YourProjectLocationForm(String postcode) {
        this.postcode = postcode;
    }

    // for Spring instantiation
    YourProjectLocationForm() {
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
