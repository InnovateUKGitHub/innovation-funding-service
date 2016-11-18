package com.worth.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

public class RoleResource {
    private Long id;
    private String name;
    private String url;
    private List<Long> processRoles = new ArrayList<>();
    private List<Long> users = new ArrayList<>();

    public RoleResource() {
    	// no-arg constructor
    }

    public RoleResource(Long id, String name, List<ProcessRoleResource> processRoles) {
        this.id = id;
        this.name = name;
        this.processRoles = simpleMap(processRoles, ProcessRoleResource::getId);
    }

    protected Boolean canEqual(Object other) {
        return other instanceof RoleResource;
    }

    public List<Long> getProcessRoles() {
        return processRoles;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProcessRoles(List<Long> processRoles) {
        this.processRoles = processRoles;
    }

    public List<Long> getUsers() {
        return this.users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        RoleResource rhs = (RoleResource) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.name, rhs.name)
            .append(this.processRoles, rhs.processRoles)
            .append(this.users, rhs.users)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(name)
            .append(processRoles)
            .append(users)
            .toHashCode();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
