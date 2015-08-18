package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class ApplicationFinance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    private Integer workingDaysPerYear;
    private String overheadAcceptRate;
    private Integer overheadRate;
    private String otherFunding;



    @ManyToOne
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    public ApplicationFinance(long id, int workingDaysPerYear, String overheadAcceptRate, int overheadRate, String otherFunding) {
        this.id = id;
        this.workingDaysPerYear = workingDaysPerYear;
        this.overheadAcceptRate = overheadAcceptRate;
        this.overheadRate = overheadRate;
        this.otherFunding = otherFunding;
    }

    public Long getId() {
        return id;
    }

    public Integer getWorkingDaysPerYear() {
        return workingDaysPerYear;
    }

    public String getOverheadAcceptRate() {
        return overheadAcceptRate;
    }

    public Integer getOverheadRate() {
        return overheadRate;
    }

    public String getOtherFunding() {
        return otherFunding;
    }
}
