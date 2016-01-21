package com.worth.ifs.finance.resource;

import com.worth.ifs.finance.domain.CostValueId;

import java.util.ArrayList;
import java.util.List;

public class CostFieldResource {
    Long id;
    String title;
    String type;
    private List<CostValueId> costValues = new ArrayList<>();

    public CostFieldResource() {
    }

    public CostFieldResource(Long id, String title, String type) {
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
}
