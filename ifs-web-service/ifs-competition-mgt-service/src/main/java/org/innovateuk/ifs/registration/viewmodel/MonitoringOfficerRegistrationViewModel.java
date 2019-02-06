package org.innovateuk.ifs.registration.viewmodel;

/**
 * Holder of model attributes for registration of monitoring officers
 */
public class MonitoringOfficerRegistrationViewModel {
    private String email;
    private String roleName;

    public MonitoringOfficerRegistrationViewModel(String email, String roleName) {
        this.email = email;
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
