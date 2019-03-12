package org.innovateuk.ifs.eugrant;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EuContactResource {

    private Long id;

    private String name;

    private String jobTitle;

    private String email;

    private String telephone;

    private boolean notified;

    public EuContactResource() {
    }

    public EuContactResource(Long id, String name, String jobTitle, String email, String telephone, boolean notified) {
        this.id = id;
        this.name = name;
        this.jobTitle = jobTitle;
        this.email = email;
        this.telephone = telephone;
        this.notified = notified;
    }

    public EuContactResource(String name, String jobTitle, String email, String telephone) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.email = email;
        this.telephone = telephone;
        this.notified = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean getNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EuContactResource that = (EuContactResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(email, that.email)
                .append(jobTitle, that.jobTitle)
                .append(telephone, that.telephone)
                .append(notified, that.notified)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(email)
                .append(jobTitle)
                .append(telephone)
                .append(notified)
                .toHashCode();
    }
}
