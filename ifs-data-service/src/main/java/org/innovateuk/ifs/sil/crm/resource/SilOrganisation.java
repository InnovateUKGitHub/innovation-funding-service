package org.innovateuk.ifs.sil.crm.resource;

public class SilOrganisation {

    private String name;
    private String registrationNumber;
    private SilAddress registeredAddress;

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

}
