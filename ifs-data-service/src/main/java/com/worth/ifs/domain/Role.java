package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wouter on 29/07/15.
 */
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;

    @OneToMany
    @JoinColumn(name="roleId", referencedColumnName="id")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();
}
