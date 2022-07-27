package org.innovateuk.ifs.application.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;

@AllArgsConstructor
@Getter
public enum ApplicationEoiEvidenceEvent  implements ProcessEvent {
    UNSUBMIT("not-submitted"),
    SUBMIT("submitted");

    private final String type;
}