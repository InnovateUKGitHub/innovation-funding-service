package com.worth.ifs.invite.resource;

public class NewUserStagedInviteResource extends StagedInviteResource {

    private String name;
    private long innovationCategoryId;

    public NewUserStagedInviteResource(String email, long competitionId, String name, long innovationCategoryId) {
        super(email, competitionId);
        this.name = name;
        this.innovationCategoryId = innovationCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInnovationCategoryId() {
        return innovationCategoryId;
    }

    public void setInnovationCategoryId(long innovationCategoryId) {
        this.innovationCategoryId = innovationCategoryId;
    }
}
