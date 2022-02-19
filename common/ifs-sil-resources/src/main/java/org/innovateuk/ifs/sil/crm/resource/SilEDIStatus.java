package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.sil.crm.resource.json.EDIStatusDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.EDIStatusSerializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeSerializer;
import org.innovateuk.ifs.user.resource.EDIStatus;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class SilEDIStatus {
    @JsonProperty(value = "ediStatus", required = true)
    @NotNull(message = "{validation.sil.edi.ediStatus.required}")
    @JsonSerialize(using = EDIStatusSerializer.class)
    @JsonDeserialize(using = EDIStatusDeserializer.class)
    private EDIStatus ediStatus;


    @JsonProperty(value = "ediReviewDate", required = true)
    @NotNull(message = "{validation.sil.edi.lastReviewDate.required}")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime ediReviewDate;



}