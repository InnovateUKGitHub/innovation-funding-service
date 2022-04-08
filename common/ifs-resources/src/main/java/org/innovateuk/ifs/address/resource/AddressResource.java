package org.innovateuk.ifs.address.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilterNot;

public class AddressResource implements Serializable {

    private static final long serialVersionUID = 4820623474620690511L;

    @NotBlank(message = "{validation.standard.addressline1.required}")
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    @NotBlank(message = "{validation.standard.town.required}")
    private String town;
    private String county;
    private String country;
    @NotBlank(message = "{validation.standard.postcode.required}")
    @Size(max = 9, message = "{validation.standard.postcode.length}")
    private String postcode;

    public AddressResource() {
        // no-arg constructor
    }

    public AddressResource(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode) {
        this(addressLine1, addressLine2, addressLine3, town, county, postcode, null);
    }

    public AddressResource(String addressLine1, String addressLine2, String addressLine3, String town, String county, String postcode, String country) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.town = town;
        this.county = county;
        this.postcode = postcode;
        this.country = country;
    }

    // For international addresses
    public AddressResource(String addressLine1, String addressLine2, String town, String country, String zipCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.town = town;
        this.country = country;
        this.postcode = zipCode;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @JsonIgnore
    public String getCombinedString() {
        String[] location = new String[3];
        location[0] = getPostcode();
        location[1] = getAddressLine1();
        location[2] = getTown();
        return String.join(", ", location);

    }

    @JsonIgnore
    public String getAsSingleLine() {
        if (getAddressLine1() == null && getTown() == null && getPostcode() == null) {
            return "";
        }
        String[] location = new String[3];
        location[0] = getAddressLine1() == null ? "" : getAddressLine1();
        location[1] = getTown() == null ? "" : getTown();
        location[2] = getPostcode() == null ? "" : getPostcode();
        return String.join(", ", location);
    }

    @JsonIgnore
    public String getAsInternationalTwoLine() {
        if (getAddressLine1() == null && getTown() == null && getPostcode() == null && getCountry() == null) {
            return "";
        }
        List<String> location = newArrayList();
        location.add(getAddressLine1());
        location.add(getTown());
        location.add("<br/>" + getCountry());
        if (!isNullOrEmpty(getPostcode())) {
            location.add(getPostcode());
        }
        return String.join(", ", location);
    }

    @JsonIgnore
    public List<String> getNonEmptyLines() {
        List<String> lines = asList(addressLine1, addressLine2, addressLine3, town, county, postcode, country);
        return simpleFilterNot(lines, StringUtils::isEmpty);
    }

    // for the project-setup-mgt MO page for international lead address,
    // standard pattern will be decided in a separate cleanup ticket
    @JsonIgnore
    public List<String> getNonEmptyLinesInternational() {
        List<String> lines = asList(addressLine1, addressLine2, addressLine3, town, county, country, postcode);
        return simpleFilterNot(lines, StringUtils::isEmpty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AddressResource that = (AddressResource) o;

        return new EqualsBuilder()
                .append(addressLine1, that.addressLine1)
                .append(addressLine2, that.addressLine2)
                .append(addressLine3, that.addressLine3)
                .append(town, that.town)
                .append(county, that.county)
                .append(postcode, that.postcode)
                .append(country, that.country)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(addressLine1)
                .append(addressLine2)
                .append(addressLine3)
                .append(town)
                .append(county)
                .append(postcode)
                .append(country)
                .toHashCode();
    }
}
