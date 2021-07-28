package org.innovateuk.ifs.sil.crm.resource;
import lombok.*;

@Getter
@Setter
@ToString
public class SilOrganisation {

    private String name;
    private String registrationNumber;
    private SilAddress registeredAddress;
    private String srcSysOrgId;

}
