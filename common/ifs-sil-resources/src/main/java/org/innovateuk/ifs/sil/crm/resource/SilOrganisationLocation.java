package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SilOrganisationLocation {

    @JsonProperty("organisationID")
    private Integer organisationID;

    @JsonProperty("organisationName")
    private String organisationName;

    @JsonProperty("companiesHouseNo")
    private String companiesHouseNo;

    @JsonProperty("internationalRegistrationNumber")
    private String internationalRegistrationNumber;

    @JsonProperty("organisationSize")
    private String organisationSize;

    @JsonProperty("internationalLocation")
    private String internationalLocation;

    @JsonProperty("workPostcode")
    private String workPostcode;
}
