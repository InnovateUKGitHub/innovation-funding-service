package org.innovateuk.ifs.eugrant.contact.viewmodel;

public class EuContactFormViewModel {

    private String name;
    private String jobTitle;
    private String email;
    private String telephone;

    public EuContactFormViewModel(String name, String jobTitle, String email, String telephone) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.email = email;
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
