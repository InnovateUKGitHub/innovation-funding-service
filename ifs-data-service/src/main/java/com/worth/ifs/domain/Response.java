package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Response {
    public Response(Long id, Date date, String value, Boolean markedAsComplete, UserApplicationRole userApplicationRole, Question question, Application app) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.markedAsComplete = markedAsComplete;
        this.userApplicationRole = userApplicationRole;
        this.question = question;
        this.application = app;
    }

    public Response () {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;
    @Column(length=5000)
    private String value;
    private Boolean markedAsComplete;

    @ManyToOne
    @JoinColumn(name="userApplicationRoleId", referencedColumnName="id")
    private UserApplicationRole userApplicationRole;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;


    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public Boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    @JsonIgnore
    public UserApplicationRole getUserApplicationRole() {
        return userApplicationRole;
    }


    public Question getQuestion() {
        return question;
    }

    public void setMarkedAsComplete(boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public void setApplication(Application application) {
        this.application = application;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setUserApplicationRole(UserApplicationRole userApplicationRole) {
        this.userApplicationRole = userApplicationRole;
    }
}
