package org.innovateuk.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Section defines database relations and a model to use client side and server side.
 */
@Entity
public class Section implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column( length = 5000 )
    private String description;

    @Column( length = 5000 )
    private String assessorGuidanceDescription;

    private Integer priority;

    private boolean questionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    @OrderBy("priority ASC")
    private List<Question> questions = new ArrayList<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parentSectionId", referencedColumnName="id")
    @JsonBackReference
    private Section parentSection;

    @OneToMany(mappedBy="parentSection",fetch=FetchType.LAZY)
    @JsonManagedReference
    @OrderBy("priority ASC")
    private List<Section> childSections;

    @Column(nullable = false)
    private boolean displayInAssessmentApplicationSummary = false;

    @Enumerated(EnumType.STRING)
    @Column(name="section_type")
    private SectionType type = SectionType.GENERAL;

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Get questions from this section and childSections.
     */
    @JsonIgnore
    public List<Question> fetchAllQuestionsAndChildQuestions() {
        List<Section> nonEmptyChildSections = simpleFilterNot(childSections, child -> child.getChildSections().isEmpty() && child.getQuestions().isEmpty());
        List<List<Question>> allChildQuestions = simpleMap(nonEmptyChildSections, Section::fetchAllQuestionsAndChildQuestions);
        return combineLists(questions, flattenLists(allChildQuestions));
    }

    /**
     * Get questions of type LEAD_ONLY
     */
    public List<Question> getLeadQuestions() {
        return questions.stream().filter(question -> question.isType(QuestionType.LEAD_ONLY)).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public Section getParentSection() {
        return parentSection;
    }

    public List<Section> getChildSections() {
        return childSections;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    public void setChildSections(List<Section> childSections) {
        this.childSections = childSections;
    }

    public Boolean hasChildSections() {
        return this.childSections!= null && !this.childSections.isEmpty();
    }

    public void setParentSection(Section parentSection) {
        this.parentSection = parentSection;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(Section o) {
        return this.getId().compareTo(o.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Section that = (Section) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getPriority() {
        return priority;
    }

    public String getAssessorGuidanceDescription() { return assessorGuidanceDescription; }

    public boolean isType(SectionType queriedType) {
    	return queriedType.equals(type);
    }
    
    public void setAssessorGuidanceDescription(String assessorGuidanceDescription) { this.assessorGuidanceDescription = assessorGuidanceDescription; }

    public boolean isDisplayInAssessmentApplicationSummary() {
        return displayInAssessmentApplicationSummary;
    }

    public boolean isQuestionGroup() {
        return questionGroup;
    }

    public void setQuestionGroup(boolean questionGroup) {
        this.questionGroup = questionGroup;
    }

    public void setDisplayInAssessmentApplicationSummary(boolean displayInAssessmentApplicationSummary) {
        this.displayInAssessmentApplicationSummary = displayInAssessmentApplicationSummary;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setType(SectionType type) {
        this.type = type;
    }
    
    public SectionType getType() {
        return type;
    }
}
