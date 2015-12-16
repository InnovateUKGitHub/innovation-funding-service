package com.worth.ifs.commons.resource;

import javax.annotation.Resource;

public class ResourceStatusError {
    private String name;
    private String description;

    public ResourceStatusError() {}

    public ResourceStatusError(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
