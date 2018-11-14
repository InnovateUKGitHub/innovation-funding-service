package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.form;

/**
 * TODO DW - document this class
 */
public class YourProjectLocationForm {

    private String postcode;

    YourProjectLocationForm(String postcode) {
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
