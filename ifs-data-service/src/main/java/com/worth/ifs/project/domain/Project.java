package com.worth.ifs.project.domain;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *  A project represents an application that has been accepted (and is now in project setup phase).
 *  It stores details specific to project (which are different from application)
 */
@Entity
public class Project {
    @Id
    private Long id;
    private LocalDate targetStartDate;

    @OneToOne
    @JoinColumn(name="address", referencedColumnName="id")
    private Address address;

    @Min(0)
    private Long durationInMonths; // in months

    private String name;

    @OneToOne
    @JoinColumn(name="projectManager", referencedColumnName="id")
    private ProcessRole projectManager;

    @OneToMany(mappedBy="project")
    private List<ProjectUser> projectUsers = new ArrayList<>();

    public Project() {}

    public Project(Long id, LocalDate targetStartDate, Address address, Long durationInMonths, ProcessRole projectManager, String name) {
        this.id = id;
        this.targetStartDate = targetStartDate;
        this.address = address;
        this.durationInMonths = durationInMonths;
        this.projectManager = projectManager;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
    }

    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public ProcessRole getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProcessRole projectManager) {
        this.projectManager = projectManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }
}
