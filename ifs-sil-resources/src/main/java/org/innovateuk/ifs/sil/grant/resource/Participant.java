package org.innovateuk.ifs.sil.grant.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageDeserializer;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageSerializer;

import java.math.BigDecimal;
import java.util.Collection;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Participant {

    private Long orgId;
    private String orgType;
    private String orgProjectRole;
    private String orgSize;

    @JsonSerialize(using = ToStringSerializer.class)
    private long contactId;

    private String contactRole;

    private String contactEmail;


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
    private Collection<Forecast> forecasts;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
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

    public String getOrgSize() {
        return orgSize;
    }

    public void setOrgSize(String size) {
        this.orgSize = orgSize;
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

    public Collection<Forecast> getForecasts() {
        return forecasts;
    }

    public void setForecasts(Collection<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public static Participant createProjectTeamParticipant(
            long orgId,
            String orgType,
            String orgProjectRole,
            long contactId,
            String contactRole,
            String contactEmail,
            String orgSize,
            BigDecimal capLimit,
            BigDecimal awardRate,
            BigDecimal overheadRate,
            Collection<Forecast> forecasts) {

        Participant projectTeamParticipant = new Participant();
        projectTeamParticipant.orgId = orgId;
        projectTeamParticipant.orgType = orgType;
        projectTeamParticipant.orgProjectRole = orgProjectRole;
        projectTeamParticipant.contactId = contactId;
        projectTeamParticipant.contactRole = contactRole;
        projectTeamParticipant.contactEmail = contactEmail;
        projectTeamParticipant.orgSize = orgSize;
        projectTeamParticipant.capLimit = capLimit;
        projectTeamParticipant.awardRate = awardRate;
        projectTeamParticipant.overheadRate = overheadRate;
        projectTeamParticipant.forecasts = forecasts;

        return projectTeamParticipant;
    }

    public static Participant createSimpleContactParticipant(
            long contactId,
            String contactRole,
            String contactEmail) {

        Participant simpleContactParticipant = new Participant();
        simpleContactParticipant.contactId = contactId;
        simpleContactParticipant.contactRole = contactRole;
        simpleContactParticipant.contactEmail = contactEmail;

        return simpleContactParticipant;
    }
}
