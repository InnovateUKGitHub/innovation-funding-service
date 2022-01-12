package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class SilLoanApplication {
    @JsonProperty("appID")
    @NotNull
    private Integer applicationID;

    @JsonProperty("appSubDate")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime applicationSubmissionDate;

    @JsonProperty("appName")
    private String applicationName;

    @JsonProperty("appLoc")
    private String applicationLocation;

    @JsonProperty("compCode")
    private String competitionCode;

    @JsonProperty("compName")
    private String competitionName;

    @JsonProperty("projectDuration")
    private Integer projectDuration;

    @JsonProperty("projTotalCost")
    private Double projectTotalCost;

    @JsonProperty("projOtherFunding")
    private Double projectOtherFunding;

    @JsonProperty("markedIneligible")
    private Boolean markedIneligible;

    @JsonProperty("eligibilityStatusChangeDate")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime eligibilityStatusChangeDate;

    @JsonProperty("eligibilityStatusChangeSource")
    private String eligibilityStatusChangeSource;
}
