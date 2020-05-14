package org.innovateuk.ifs.management.registration.viewmodel;

public class CompetitionFinanceRegistrationViewModel {

    private String email;
    private String roleName;

    public CompetitionFinanceRegistrationViewModel(String email, String roleName) {
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
