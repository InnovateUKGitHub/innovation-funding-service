package org.innovateuk.ifs.sil.grant.resource;

import java.math.BigDecimal;
import java.util.Set;

public class Participant {
    private long id;
    private String orgType;
    private String orgProjectRole;
    private String contactRole;
    private String contactEmail;
    private int size;
    private BigDecimal capLimit;
    private BigDecimal awardRate;
    private BigDecimal overheadRate;
    private String status;
    private Set<Forecast> forecasts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrgProjectRole() {
        return orgProjectRole;
    }

    public void setOrgProjectRole(String orgProjectRole) {
        this.orgProjectRole = orgProjectRole;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getContactRole() {
        return contactRole;
    }

    public void setContactRole(String contactRole) {
        this.contactRole = contactRole;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public BigDecimal getCapLimit() {
        return capLimit;
    }

    public void setCapLimit(BigDecimal capLimit) {
        this.capLimit = capLimit;
    }

    public BigDecimal getAwardRate() {
        return awardRate;
    }

    public void setAwardRate(BigDecimal awardRate) {
        this.awardRate = awardRate;
    }

    public BigDecimal getOverheadRate() {
        return overheadRate;
    }

    public void setOverheadRate(BigDecimal overheadRate) {
        this.overheadRate = overheadRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(Set<Forecast> forecasts) {
        this.forecasts = forecasts;
    }
}
