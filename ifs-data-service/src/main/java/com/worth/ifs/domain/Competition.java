package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Competition defines database relations and a model to use client side and server side.
 */

@Entity
public class Competition {
    public Competition() {
    }
    public Competition(long id, List<Application> applications, List<Question> questions, List<Section> sections, String name, String description, Date deadline) {
        this.id = id;
        this.applications = applications;
        this.questions = questions;
        this.sections = sections;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }
    public Competition(long id, String name, String description, Date deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

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



    @Lob
    @Column( length = 5000 )
    private String description;
    private Date deadline;

    public List<Section> getSections() {
        return sections;
    }

    public String getDescription() {
        return description;
    }


    public void addApplication(Application... apps){
        if(applications == null){
            applications = new ArrayList<>();
        }
        this.applications.addAll(Arrays.asList(apps));
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public Date getDeadline() {
        return deadline;
    }
}
