package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.user.resource.BusinessType;

/**
 * Holder of model attributes for the assessors shown in the 'Invite Assessors' view.
 */
public class AvailableAssessorViewModel {

    private long userId;
    private String name;
    private String email;
    private BusinessType businessType;
    private String innovationArea;
    private boolean compliant;
    private boolean added;

    public AvailableAssessorViewModel(long userId, String name, String email, BusinessType businessType, String innovationArea, boolean compliant, boolean added) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.businessType = businessType;
        this.innovationArea = innovationArea;
        this.compliant = compliant;
        this.added = added;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BusinessType getBusinessType() {
        return businessType;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public boolean isCompliant() {
        return compliant;
    }

    public boolean isAdded() {
        return added;
    }
}
