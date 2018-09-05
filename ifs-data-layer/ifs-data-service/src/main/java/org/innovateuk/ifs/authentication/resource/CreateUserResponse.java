package org.innovateuk.ifs.authentication.resource;

/**
 * Represents the return result from creating a User
 */
public class CreateUserResponse {

    private String uuid;
    private String email;
    private String created;
    private String modified;

    // for JSON marshalling
    @SuppressWarnings("unused")
    CreateUserResponse() {
    }

    public CreateUserResponse(String uuid, String email, String created, String modified) {
        this.uuid = uuid;
        this.email = email;
        this.created = created;
        this.modified = modified;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
