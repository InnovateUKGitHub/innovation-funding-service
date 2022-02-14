package org.innovateuk.ifs.sil.crm.resource;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
public class SilContact {
    private String ifsUuid;
    private String experienceType;
    private String ifsAppID;
    private String email;
    private String lastName;
    private String firstName;
    private String title;
    private String jobTitle;
    @JsonProperty("Address")
    private SilAddress address;
    private SilOrganisation organisation;
    private final String sourceSystem = "IFS";
    private String srcSysContactId;
    private String phoneNumber;

}
