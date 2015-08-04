package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    public Competition(long id, String name, String description, Date deadline) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
    }

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

    public void addApplication(Application app){
        applications.add(app);
    }
}
