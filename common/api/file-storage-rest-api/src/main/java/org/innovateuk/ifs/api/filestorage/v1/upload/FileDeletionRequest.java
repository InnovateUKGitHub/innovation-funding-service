package org.innovateuk.ifs.api.filestorage.v1.upload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDeletionRequest implements Serializable {

    @NotNull
    /** uuid reference for the file */
    private String fileId;

}
