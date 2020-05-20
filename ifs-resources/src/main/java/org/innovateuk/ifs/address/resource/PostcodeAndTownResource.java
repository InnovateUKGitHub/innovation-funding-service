package org.innovateuk.ifs.address.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostcodeAndTownResource {
    private String postcode;
    private String town;

    public PostcodeAndTownResource(){}

    public PostcodeAndTownResource(String postcode, String town) {
        this.postcode = postcode;
        this.town = town;
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
