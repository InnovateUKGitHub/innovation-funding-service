package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.innovateuk.ifs.application.resource.QuestionStatus;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.sil.crm.resource.json.QuestionStatusDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.QuestionStatusSerializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeDeserializer;
import org.innovateuk.ifs.sil.crm.resource.json.ZonedDateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class SilLoanApplicationStatus {
    @JsonProperty(value = "questionSetupType", required = true)
    @NotNull(message = "{validation.sil.loans.questionSetupType.required}")
    private QuestionSetupType questionSetupType;

    @JsonProperty(value = "completionStatus", required = true)
    @JsonSerialize(using = QuestionStatusSerializer.class)
    @JsonDeserialize(using = QuestionStatusDeserializer.class)
    private QuestionStatus completionStatus;

    @JsonProperty(value = "completionDate", required = true)
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @NotNull(message = "{validation.sil.loans.completionDate.required}")
    private ZonedDateTime completionDate;

    @JsonIgnore
    public boolean isStatusComplete() {
        return QuestionStatus.COMPLETE == completionStatus;
    }
    @JsonIgnore
    public boolean isStatusIncomplete() {
        return QuestionStatus.INCOMPLETE == completionStatus;
    }
}