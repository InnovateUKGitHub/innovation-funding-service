package org.innovateuk.ifs.application.forms.form;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form used to capture EOI evidence
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class EoiEvidenceForm {

    private String evidenceFileEntryName;
    private Long evidenceFileEntryId;
    private MultipartFile eoiEvidenceFile;
}