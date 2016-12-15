package org.innovateuk.ifs.management.viewmodel;

/**
 * Abstract holder of model attributes for the assessors shown in the 'Invite Assessors' view.
 */
abstract class InviteAssessorsRowViewModel {

    private String name;
    private String innovationArea;
    private boolean compliant;

    protected InviteAssessorsRowViewModel(String name, String innovationArea, boolean compliant) {
        this.name = name;
        this.innovationArea = innovationArea;
        this.compliant = compliant;
    }

    public String getName() {
        return name;
    }

    public String getInnovationArea() {
        return innovationArea;
    }

    public boolean isCompliant() {
        return compliant;
    }
}