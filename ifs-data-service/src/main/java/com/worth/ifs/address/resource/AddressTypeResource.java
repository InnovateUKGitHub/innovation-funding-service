package com.worth.ifs.address.resource;

import org.hibernate.validator.constraints.NotBlank;

public class AddressTypeResource {
    private Long id;

    @NotBlank
    private String name;

    public AddressTypeResource() {
    }

    public AddressTypeResource(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
