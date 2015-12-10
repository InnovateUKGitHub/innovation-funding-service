package com.worth.ifs.competition.resource;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;
import com.worth.ifs.competition.domain.Competition;
import org.springframework.hateoas.core.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Relation(value="competition", collectionRelation="competitions")
public class CompetitionResourceHateoas extends ResourceWithEmbeddeds {
    private Long id;
    private List<Section> sections = new ArrayList<>();
    private List<Application> applications = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private String name;
    private String description;
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate startDate;
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate endDate;
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate assessmentStartDate;
    @JsonSerialize(using= LocalDateSerializer.class)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    private LocalDate assessmentEndDate;

    public CompetitionResourceHateoas() {
    }

    public CompetitionResourceHateoas(Long id,
                                      List<Application> applications,
                                      List<Question> questions,
                                      List<Section> sections,
                                      String name,
                                      String description,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LocalDate assessmentStartDate,
                                      LocalDate assessmentEndDate) {
        super();
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.assessmentStartDate = assessmentStartDate;
        this.assessmentEndDate = assessmentEndDate;
    }

    public Competition toCompetition(){
        Competition competition =  new Competition(id, applications, questions, sections, name, description, startDate.atStartOfDay(), endDate.atStartOfDay());
        competition.setAssessmentStartDate(assessmentStartDate);
        competition.setAssessmentEndDate(assessmentEndDate);
        return competition;
    }

    public String getName(){
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public LocalDate getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Section> getSections() {
        return sections;
    }
}
