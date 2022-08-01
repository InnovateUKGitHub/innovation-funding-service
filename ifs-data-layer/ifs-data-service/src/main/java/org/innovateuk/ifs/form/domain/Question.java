package org.innovateuk.ifs.form.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;

/**
 * Question defines database relations and a model to use client side and server side.
 */
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String shortName;

    @Column(length = 5000, columnDefinition = "LONGTEXT")
    private String description;

    @Column(length = 5000, columnDefinition = "LONGTEXT")
    private String description2;

    private Boolean markAsCompletedEnabled = false;

    private Boolean assignEnabled = true;

    private Boolean multipleStatuses = false;

    private Integer priority;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FormInput> formInputs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competitionId", referencedColumnName = "id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sectionId", referencedColumnName = "id")
    private Section section;

    private String questionNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name="question_type")
    private QuestionType type = QuestionType.GENERAL;

    @Enumerated(EnumType.STRING)
    private QuestionSetupType questionSetupType;

    private Integer assessorMaximumScore;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "questionnaire_id", referencedColumnName = "id")
    private Questionnaire questionnaire;

    @Column(name="pre_registration")
    private boolean enabledForPreRegistration = false;

    public Question() {
        //default constructor
    }

    public Integer getAssessorMaximumScore() {
        return assessorMaximumScore;
    }

    public void setAssessorMaximumScore(Integer assessorMaximumScore) {
        this.assessorMaximumScore = assessorMaximumScore;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    @JsonIgnore
    public Section getSection() {
        return section;
    }

    public Boolean isMarkAsCompletedEnabled() {
        return markAsCompletedEnabled == null ? false : markAsCompletedEnabled;
    }

    public Boolean hasMultipleStatuses() {
        return multipleStatuses;
    }

    public Boolean getMultipleStatuses() {
        return multipleStatuses;
    }

    public Boolean getMarkAsCompletedEnabled() {
        return markAsCompletedEnabled;
    }

    public Boolean isAssignEnabled() {
        // never return a null value.. it is enabled or disabled.
        return assignEnabled == null ? true : assignEnabled;
    }

    public void setAssignEnabled(Boolean assignEnabled) {
        this.assignEnabled = assignEnabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public List<FormInput> getFormInputs() {
        return formInputs;
    }

    public Boolean getAssignEnabled() {
        return this.assignEnabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public void setMarkAsCompletedEnabled(Boolean markAsCompletedEnabled) {
        this.markAsCompletedEnabled = markAsCompletedEnabled;
    }

    public void setMultipleStatuses(Boolean multipleStatuses) {
        this.multipleStatuses = multipleStatuses;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setFormInputs(List<FormInput> formInputs) {
        this.formInputs = formInputs;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    public QuestionType getType() {
		return type;
	}
    
    public void setType(QuestionType type) {
		this.type = type;
	}
    
    public boolean isType(QuestionType queriedType) {
    	return queriedType.equals(type);
    }

    public QuestionSetupType getQuestionSetupType() {
        return questionSetupType;
    }

    public void setQuestionSetupType(QuestionSetupType questionSetupType) {
        this.questionSetupType = questionSetupType;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public boolean isScope() {
        return this.questionSetupType == QuestionSetupType.SCOPE;
    }

    public boolean isCompetitionOpen() {
        return competition.getCompetitionStatus().isLaterThan(READY_TO_OPEN);
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public boolean isEnabledForPreRegistration() {
        return enabledForPreRegistration;
    }

    public void setEnabledForPreRegistration(boolean enabledForPreRegistration) {
        this.enabledForPreRegistration = enabledForPreRegistration;
    }
}
