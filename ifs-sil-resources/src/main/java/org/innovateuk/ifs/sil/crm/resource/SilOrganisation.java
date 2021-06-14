package org.innovateuk.ifs.sil.crm.resource;

public class SilOrganisation {

    private String name;
    private String registrationNumber;
    private SilAddress registeredAddress;
    private String srcSysOrgId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public SilAddress getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(SilAddress registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public String getSrcSysOrgId() {
        return srcSysOrgId;
    }

    public void setSrcSysOrgId(String srcSysOrgId) {
        this.srcSysOrgId = srcSysOrgId;
    }
}
