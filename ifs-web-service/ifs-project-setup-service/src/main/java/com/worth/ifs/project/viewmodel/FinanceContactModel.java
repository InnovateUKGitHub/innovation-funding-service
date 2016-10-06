package com.worth.ifs.project.viewmodel;

// model for selecting finance contacts

public class FinanceContactModel {
    private String name;
    private String status;
    private Long id;

    public FinanceContactModel(final FinanceContactStatus status, final String name, final Long id) {
        this.status = status.name();
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }
}
