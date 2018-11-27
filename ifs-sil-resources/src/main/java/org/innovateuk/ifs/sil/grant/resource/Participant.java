package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageDeserializer;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageSerializer;

import java.math.BigDecimal;
import java.util.List;

public class Participant {
    @JsonProperty("orgId")
    private long id;
    private String orgType;
    private String orgProjectRole;
    private long contactId;
    private String contactRole;
    private String contactEmail;
    @JsonProperty("orgSize")
    private String size;

    @JsonSerialize(using = PercentageSerializer.class)
    @JsonDeserialize(using = PercentageDeserializer.class)
    private BigDecimal capLimit;
    @JsonSerialize(using = PercentageSerializer.class)
    @JsonDeserialize(using = PercentageDeserializer.class)
    private BigDecimal awardRate;
    @JsonSerialize(using = PercentageSerializer.class)
    @JsonDeserialize(using = PercentageDeserializer.class)
    private BigDecimal overheadRate;
    @JsonProperty("forecast")
    private List<Forecast> forecasts;

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

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
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

    public List<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }
}
