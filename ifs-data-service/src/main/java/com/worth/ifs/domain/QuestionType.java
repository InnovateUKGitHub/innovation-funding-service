package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.List;

@Entity
public class QuestionType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @JsonIgnore
    @OneToMany(mappedBy="questionType")
    private List<Question> questions;

    String title;
}
