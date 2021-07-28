package org.innovateuk.ifs.sil.crm.resource;
import lombok.*;

@Getter
@Setter
@ToString
public class SilAddress {

    private String buildingName;
    private String street;
    private String locality;
    private String town;
    private String postcode;
    private String country;

}
