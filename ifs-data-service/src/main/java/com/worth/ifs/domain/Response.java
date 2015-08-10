package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Response {
    public Response(long id, Date date, String value, boolean markedAsComplete, UserApplicationRole userApplicationRole, Question question) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.markedAsComplete = markedAsComplete;
        this.userApplicationRole = userApplicationRole;
        this.question = question;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date date;
    private String value;
    private boolean markedAsComplete;

    @ManyToOne
    @JoinColumn(name="userApplicationRoleId", referencedColumnName="id")
    private UserApplicationRole userApplicationRole;

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
    public UserApplicationRole getUserApplicationRoles() {
        return userApplicationRole;
    }

    @JsonIgnore
    public Question getQuestion() {
        return question;
    }
}
