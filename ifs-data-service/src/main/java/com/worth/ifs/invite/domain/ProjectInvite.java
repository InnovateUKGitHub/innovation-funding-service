package com.worth.ifs.invite.domain;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

//@Entity
//@DiscriminatorValue(value = "PROJECT")
public class ProjectInvite extends Invite<Organisation, Project> {


    @Override
    public Organisation getOwner() {
        return null;
    }

    @Override
    public void setOwner(Organisation inviter) {

    }

    @Override
    public Project getTarget() {
        return null;
    }

    @Override
    public void setTarget(Project target) {

    }
}
