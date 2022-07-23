package org.innovateuk.ifs.application.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Eoi evidence submission status
 */
@Getter
@AllArgsConstructor
public enum EoiEvidenceStatus {
    SUBMITTED("Submitted"),
    NOT_SUBMITTED("Not Submitted");

    private String name;
}
