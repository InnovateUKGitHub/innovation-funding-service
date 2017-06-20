package org.innovateuk.ifs.authentication.resource;

/**
 * Represents a request to an Identity Provider to create a new User record
 */
public class CreateUserResource {

    private String email;
    private String password;

    /**
     * For JSON marshalling
     */
    public CreateUserResource() {
    	// no-arg constructor
    }

    public CreateUserResource(String email, String password) {
        this.email= email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateUserResource that = (CreateUserResource) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
