package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDeletionResponse {

    @NotNull
    /** uuid reference for the file */
    private String fileId;

}
