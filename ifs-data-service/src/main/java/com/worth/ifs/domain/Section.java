package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by wouter on 30/07/15.
 */

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @OneToMany(mappedBy="section")
    private List<Question> questions;

    private String name;
}
