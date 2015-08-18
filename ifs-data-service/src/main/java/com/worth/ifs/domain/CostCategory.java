package com.worth.ifs.domain;

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

    @OneToOne
    private Question question;

    public Long getId() {
        return id;
    }

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
