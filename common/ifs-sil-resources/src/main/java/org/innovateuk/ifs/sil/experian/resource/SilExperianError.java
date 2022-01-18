package org.innovateuk.ifs.sil.experian.resource;

public class SilExperianError {
    private String code;
    private String type;
    private String message;
    private String fields;
    private String description;

    public SilExperianError() {}

    public SilExperianError(String code, String type, String message, String fields, String description) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.fields = fields;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
