package org.innovateuk.ifs.application.resource;

import lombok.*;
import org.innovateuk.ifs.workflow.resource.State;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEoiEvidenceResponseResource {
    private Long id;
    private Long applicationId;
    private Long organisationId;
    private Long fileEntryId;
    private State fileState;

    public ApplicationEoiEvidenceResponseResource(Long applicationId, Long organisationId, Long fileEntryId) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
        this.fileEntryId = fileEntryId;
    }
}
