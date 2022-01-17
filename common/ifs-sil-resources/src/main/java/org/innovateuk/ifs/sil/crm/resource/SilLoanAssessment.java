package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class SilLoanAssessment {
    @JsonProperty("competitionID")
    @NotNull
    private Long competitionID;

    @JsonProperty("applications")
    @NotNull
    private List<SilLoanAssessmentRow> applications;
}
