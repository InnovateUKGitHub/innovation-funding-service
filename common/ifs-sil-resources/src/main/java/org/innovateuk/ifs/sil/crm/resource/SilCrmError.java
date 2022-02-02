package org.innovateuk.ifs.sil.crm.resource;
import lombok.*;

@Getter
@Setter
@ToString
public class SilCrmError {
    private Integer code;
    private String message;
    private String fields;

}
