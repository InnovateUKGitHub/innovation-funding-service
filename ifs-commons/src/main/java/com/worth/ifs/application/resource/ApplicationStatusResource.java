package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ApplicationStatusResource {

    public ApplicationStatusResource() {
    	// no-arg constructor
    }

    public ApplicationStatusResource(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
