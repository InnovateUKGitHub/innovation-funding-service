package org.innovateuk.ifs.sil.grant.resource;

import java.util.Set;

public class Participant {
    private String id;
    private String type;
    private String role;
    private String contactEmail;
    private String size;
    private String capLimit;
    private String awardRate;
    private String overheadRate;
    private String status;
    private Set<Forecast> forecasts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCapLimit() {
        return capLimit;
    }

    public void setCapLimit(String capLimit) {
        this.capLimit = capLimit;
    }

    public String getAwardRate() {
        return awardRate;
    }

    public void setAwardRate(String awardRate) {
        this.awardRate = awardRate;
    }

    public String getOverheadRate() {
        return overheadRate;
    }

    public void setOverheadRate(String overheadRate) {
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
