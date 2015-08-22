package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The costs needs to be grouped. With the relation to question it can be seen as one group which can live in a section.
 */
@Entity
public class CostCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="applicationFinanceId", referencedColumnName="id")
    private ApplicationFinance applicationFinance;

    @OneToMany(mappedBy="costCategory")
    private List<Cost> costs = new ArrayList<Cost>();

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public CostCategory() {

    }

    public CostCategory(Long id, ApplicationFinance applicationFinance, Question question) {
        this.id = id;
        this.applicationFinance = applicationFinance;
        this.question = question;
    }

    @JsonIgnore
    public ApplicationFinance getApplicationFinance() {
        return applicationFinance;
    }

    public List<Cost> getCosts() {
        return costs;
    }

    public Question getQuestion() {
        return question;
    }
}
