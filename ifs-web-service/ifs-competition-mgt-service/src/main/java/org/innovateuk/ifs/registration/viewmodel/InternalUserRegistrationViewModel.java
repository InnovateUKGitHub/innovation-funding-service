package org.innovateuk.ifs.registration.viewmodel;

public class InternalUserRegistrationViewModel {
    private String name;
    private String roleName;
    private String email;

    public InternalUserRegistrationViewModel(String name, String roleName, String email) {
        this.name = name;
        this.roleName = roleName;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
