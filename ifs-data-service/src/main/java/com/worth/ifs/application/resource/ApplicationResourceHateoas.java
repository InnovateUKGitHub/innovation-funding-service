package com.worth.ifs.application.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.ApplicationStatus;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResourceHateoas;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.core.Relation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Relation(value="application", collectionRelation="applications")
public class ApplicationResourceHateoas extends ResourceWithEmbeddeds {
    private Long id;
    private String name;
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate startDate;
    private Long durationInMonths; // in months
    private List<ProcessRole> processRoles = new ArrayList<>();
    private ApplicationStatus applicationStatus;
    private Competition competition;

    private final Log log = LogFactory.getLog(getClass());

    public ApplicationResourceHateoas(){}

    public ApplicationResourceHateoas(Long id,
                                      String name,
                                      LocalDate startDate,
                                      Long durationInMonths,
                                      List<ProcessRole> processRoles,
                                      ApplicationStatus applicationStatus,
                                      Competition competition
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
        if(competition==null){
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            RestTemplate restTemplate = new RestTemplate();
            String href = this.getLink("competition").getHref();
            HttpHeaders headers = new HttpHeaders();
            headers.set("IFS_AUTH_TOKEN",token);
            HttpEntity entity = new HttpEntity(headers);
            log.info("getting competition with token: "+token);

            HttpEntity<CompetitionResourceHateoas> response = restTemplate.exchange(href, HttpMethod.GET, entity, CompetitionResourceHateoas.class);
            this.competition = response.getBody().toCompetition();
        }
        Application application = new Application(competition, name, processRoles, applicationStatus, id);
        application.setStartDate(this.startDate);
        return application;
    }

    public String getName() {
        return this.name;
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

    @JsonProperty("app-id")
    public Long getApplicationId(){ return id; }
}
