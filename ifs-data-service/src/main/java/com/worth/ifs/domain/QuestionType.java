package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@Entity
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy="questionType")
    private List<Question> questions;

    private String title;


    public QuestionType(long id,String title, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.questions = questions;
    }

    public QuestionType() {

    }

    public long getId() {
        return id;
    }


    @JsonIgnore
    public List<Question> getQuestions() {
        return questions;
    }

    public String getTitle() {
        return title;
    }
}
