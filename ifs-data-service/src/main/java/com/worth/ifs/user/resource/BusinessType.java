package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.User;

/**
 * A {@link User}'s main area of business. For User registration we only need to distinguish between business and
 * academic Users.
 */
public enum BusinessType {
    BUSINESS (1L, "Business"),
    ACADEMIC (2L, "Academic");

    private String displayName;
    private Long id;

    BusinessType(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getId() {
        return id;
    }
}
