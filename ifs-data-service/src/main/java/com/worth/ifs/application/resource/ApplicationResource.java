package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResource extends ResourceWithEmbeddeds {

    private Long id;
    private String name;
    private LocalDate startDate;
    private Long durationInMonths; // in months
    private List<ProcessRole> processRoles = new ArrayList<ProcessRole>();
    private ApplicationStatus applicationStatus;
    private Competition competition;

    @JsonCreator
    public ApplicationResource(@JsonProperty("id") Long id,
                               @JsonProperty("name") String name,
                               @JsonProperty("startDate") LocalDate startDate,
                               @JsonProperty("durationInMonths") Long durationInMonths,
                               @JsonProperty("processRoles") List<ProcessRole> processRoles,
                               @JsonProperty("applicationStatus") ApplicationStatus applicationStatus,
                               @JsonProperty("competition2") Competition competition
                               ){
        super();
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.durationInMonths = durationInMonths;
        this.processRoles = processRoles;
        this.applicationStatus = applicationStatus;
        this.competition = competition;
    }

    public Application toApplication() {
        return new Application(this.competition, this.name, this.processRoles, this.applicationStatus, this.id);
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public Competition getCompetition() {
        return competition;
    }
}
