package org.innovateuk.ifs.eugrant;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EuContactResource {

    private String name;

    private String jobTitle;

    private String email;

    private String telephone;

    public EuContactResource() {
    }

    public EuContactResource(String name, String jobTitle, String email, String telephone) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuContactResource that = (EuContactResource) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(email, that.email)
                .append(jobTitle, that.jobTitle)
                .append(telephone, that.telephone)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(email)
                .append(jobTitle)
                .append(telephone)
                .toHashCode();
    }
}
