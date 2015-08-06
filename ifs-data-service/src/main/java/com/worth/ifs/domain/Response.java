package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Response {
    public Response(long id, Date date, String value, boolean markedAsComplete, User user, Application application, Question question) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.markedAsComplete = markedAsComplete;
        this.user = user;
        this.application = application;
        this.question = question;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date date;
    private String value;
    private boolean markedAsComplete;

    @ManyToOne
    @JoinColumn(name="userId", referencedColumnName="id")
    private User user;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    @JsonIgnore
    public Question getQuestion() {
        return question;
    }
}
