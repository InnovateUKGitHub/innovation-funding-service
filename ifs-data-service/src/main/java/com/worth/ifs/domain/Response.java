package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Response {
    public Response(Long id, Date date, String value, Boolean markedAsComplete, UserApplicationRole userApplicationRole, Question question) {
        this.id = id;
        this.date = date;
        this.value = value;
        this.markedAsComplete = markedAsComplete;
        this.userApplicationRole = userApplicationRole;
        this.question = question;
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
}
