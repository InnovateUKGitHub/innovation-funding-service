package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ProjectTeamStatusResourceBuilder extends BaseBuilder<ProjectTeamStatusResource, ProjectTeamStatusResourceBuilder> {


    private ProjectTeamStatusResourceBuilder(List<BiConsumer<Integer, ProjectTeamStatusResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectTeamStatusResourceBuilder newProjectTeamStatusResource() {
        return new ProjectTeamStatusResourceBuilder(emptyList());
    }

    @Override
    protected ProjectTeamStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectTeamStatusResource>> actions) {
        return new ProjectTeamStatusResourceBuilder(actions);
    }

    @Override
    protected ProjectTeamStatusResource createInitial() {
        return new ProjectTeamStatusResource();
    }


    public ProjectTeamStatusResourceBuilder withPartnerStatuses(List<ProjectPartnerStatusResource> partnerStatuses){
        return with((projectTeamStatusResource) -> projectTeamStatusResource.setPartnerStatuses(partnerStatuses));
    }

}
