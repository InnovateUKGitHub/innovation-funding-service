package com.worth.ifs.invite.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.project.domain.Project;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;


public class ProjectInviteBuilder extends BaseBuilder<ProjectInvite, ProjectInviteBuilder> {

    private ProjectInviteBuilder(List<BiConsumer<Integer, ProjectInvite>> multiActions) {
        super(multiActions);
    }

    public static ProjectInviteBuilder newInvite() {
        return new ProjectInviteBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProjectInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectInvite>> actions) {
        return new ProjectInviteBuilder(actions);
    }

//    @Override
//    public void postProcess(int index, ProjectInvite invite) {
//
//        // add back-refs to InviteOrganisations
//        Project inviteProject = invite.getTarget();
//        if (inviteOrganisation != null && !simpleMap(inviteOrganisation.getInvites(), ApplicationInvite::getId).contains(invite.getId())) {
//            inviteOrganisation.getInvites().add(invite);
//        }
//    }


    @Override
    protected ProjectInvite createInitial() {
        return new ProjectInvite();
    }
}
