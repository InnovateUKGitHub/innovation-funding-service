package org.innovateuk.ifs.application.resource;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEoiEvidenceResponseResource {
    private Long applicationId;
    private Long organisationId;
    private Long fileEntryId;
}
