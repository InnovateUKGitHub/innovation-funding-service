package com.worth.ifs.user.domain;

import javax.persistence.*;

/**
 * The project_finance_emails table is a temporary measure to record users who
 * should be granted the project finance role when they register.
 * This class is the entity to map from the project_finance_emails table.
 */
@Entity
@Table(name = "project_finance_emails")
public class ProjectFinanceEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique=true)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
