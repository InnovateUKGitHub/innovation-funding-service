package org.innovateuk.ifs.finance.resource;

public class FinanceRowMetaFieldResource {
    Long id;
    String title;
    String type;

    public FinanceRowMetaFieldResource() {
    	// no-arg constructor
    }

    public FinanceRowMetaFieldResource(Long id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }
}
