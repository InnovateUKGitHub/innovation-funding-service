package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the available assessors shown in the 'Find' tab of the Assessment Panel Invite Assessors view.
 */
public class InterviewPanelApplicationRowViewModel {

    private final long id;
    private final String name;

    public InterviewPanelApplicationRowViewModel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}