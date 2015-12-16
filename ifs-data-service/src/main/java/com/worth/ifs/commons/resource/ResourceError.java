package com.worth.ifs.commons.resource;

import javax.annotation.Resource;

public class ResourceError {
    private String name;
    private String description;

    public ResourceError() {}

    public ResourceError(String name, String description) {
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
