package org.innovateuk.ifs.sil.crm.resource;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.sil.common.json.LocalDateDeserializer;
import org.innovateuk.ifs.sil.common.json.LocalDateSerializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeSerializer;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageDeserializer;
import org.innovateuk.ifs.sil.grant.resource.json.PercentageSerializer;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@JsonPropertyOrder
public class SilIMApplicationLocationInfo {
    @JsonProperty("appID")
    @NotNull
    private Integer applicationID;

    @JsonProperty("appName")
    private String applicationName;

    @JsonProperty("appStartDate")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate applicationStartDate;

    @JsonProperty("compID")
    private String competitionID;

    @JsonProperty("fundingDecision")
    private String fundingDecisionStatus;

    @JsonProperty("durationInMonths")
    private Integer durationInMonths;

    @JsonProperty("completion")
    @JsonSerialize(using = PercentageSerializer.class)
    @JsonDeserialize(using = PercentageDeserializer.class)
    private BigDecimal completionPercentage;

    @JsonProperty("manageFundingEmailDate")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime manageFundingEmailDate;

    @JsonProperty("inAssessmentReviewPanel")
    private Boolean InAssessmentReviewPanel;

    @JsonProperty("companyAge")
    private String companyAge;

    @JsonProperty("companyPrimaryFocus")
    private String companyPrimaryFocus;

    @JsonProperty("organisations")
    private List<SilOrganisationLocation> organisations;

}
