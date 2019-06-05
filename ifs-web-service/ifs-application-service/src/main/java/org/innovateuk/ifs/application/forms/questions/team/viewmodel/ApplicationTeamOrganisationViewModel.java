package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import java.util.List;

public class ApplicationTeamOrganisationViewModel {

    private final long id;
    private final String name;
    private final List<ApplicationTeamRowViewModel> rows;
    private final boolean editable;
    private final boolean existing;

    private boolean openAddTeamMemberForm;

    public ApplicationTeamOrganisationViewModel(long id, String name, List<ApplicationTeamRowViewModel> rows, boolean editable, boolean existing) {
        this.id = id;
        this.name = name;
        this.rows = rows;
        this.editable = editable;
        this.existing = existing;
        this.openAddTeamMemberForm = false;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ApplicationTeamRowViewModel> getRows() {
        return rows;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isExisting() {
        return existing;
    }

    public boolean isOpenAddTeamMemberForm() {
        return openAddTeamMemberForm;
    }

    public void setOpenAddTeamMemberForm(boolean openAddTeamMemberForm) {
        this.openAddTeamMemberForm = openAddTeamMemberForm;
    }

    public boolean isLead() {
        return rows.stream().anyMatch(ApplicationTeamRowViewModel::isLead);
    }
    public boolean isSingleUserRemaining() {
        return rows.size() == 1;
    }
}
