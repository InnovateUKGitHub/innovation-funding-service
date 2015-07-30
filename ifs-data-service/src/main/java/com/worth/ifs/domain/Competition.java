package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by wouter on 30/07/15.
 */
@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy="competition")
    private List<Application> applications;

    @OneToMany(mappedBy="competition")
    private List<Question> questions;

    @OneToMany(mappedBy="competition")
    private List<Section> sections;

    private String name;
    private String description;
    private Date deadline;
}
