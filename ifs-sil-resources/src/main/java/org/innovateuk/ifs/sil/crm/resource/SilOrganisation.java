package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.innovateuk.ifs.sil.common.json.LocalDateDeserializer;
import org.innovateuk.ifs.sil.common.json.LocalDateSerializer;

import java.time.LocalDate;
import java.util.List;

public class SilOrganisation {

    private String name;
    private String registrationNumber;
    private SilAddress registeredAddress;
    private String srcSysOrgId;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfIncorporation;
    private List<String> sicCodes;
    private List<String> executiveOfficers;

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

    public LocalDate getDateOfIncorporation() {
        return dateOfIncorporation;
    }

    public void setDateOfIncorporation(LocalDate dateOfIncorporation) {
        this.dateOfIncorporation = dateOfIncorporation;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public List<String> getExecutiveOfficers() {
        return executiveOfficers;
    }

    public void setExecutiveOfficers(List<String> executiveOfficers) {
        this.executiveOfficers = executiveOfficers;
    }
}
