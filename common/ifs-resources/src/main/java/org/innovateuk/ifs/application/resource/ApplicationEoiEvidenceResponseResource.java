package org.innovateuk.ifs.application.resource;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEoiEvidenceResponseResource {
    private Long Id;
    private Long applicationId;
    private Long organisationId;
    private Long fileEntryId;

    public ApplicationEoiEvidenceResponseResource(Long applicationId, Long organisationId, Long fileEntryId) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
        this.fileEntryId = fileEntryId;
    }
}
