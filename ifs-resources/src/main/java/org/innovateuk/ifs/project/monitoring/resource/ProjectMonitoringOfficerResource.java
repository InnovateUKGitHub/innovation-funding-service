package org.innovateuk.ifs.project.monitoring.resource;

public class ProjectMonitoringOfficerResource {

    private long id;
    private String firstName;
    private String lastName;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
