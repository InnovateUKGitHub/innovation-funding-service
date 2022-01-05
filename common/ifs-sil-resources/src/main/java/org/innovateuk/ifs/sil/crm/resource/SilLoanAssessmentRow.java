package org.innovateuk.ifs.sil.crm.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class SilLoanAssessmentRow {
    @JsonProperty("appID")
    @NotNull
    private Integer applicationID;

    @JsonProperty("scoreAverage")
    @NotNull
    private BigDecimal scoreAverage;

    @JsonProperty("scoreSpread")
    @NotNull
    private Integer scoreSpread;

    @JsonProperty("assessorNumber")
    @NotNull
    private Integer assessorNumber;

    @JsonProperty("assessorNotInScope")
    @NotNull
    private Integer assessorNotInScope;

    @JsonProperty("assessorRecommended")
    @NotNull
    private Long assessorRecommended;

    @JsonProperty("assessorNotRecommended")
    @NotNull
    private Long assessorNotRecommended;

}
