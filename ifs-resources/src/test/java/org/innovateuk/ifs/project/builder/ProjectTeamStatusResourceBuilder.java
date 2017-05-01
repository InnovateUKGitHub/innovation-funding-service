package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.addListToList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.addToList;

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


    @SafeVarargs
    public final ProjectTeamStatusResourceBuilder withPartnerStatuses(List<ProjectPartnerStatusResource>... partnerStatuses){
        return withArray((statuses, projectTeamStatusResource) -> addListToList("partnerStatuses", statuses, projectTeamStatusResource), partnerStatuses);
    }

    public ProjectTeamStatusResourceBuilder withProjectLeadStatus(ProjectPartnerStatusResource... projectLeadStatus) {
        return withArray((status, projectTeamStatusResource) -> addToList("partnerStatuses", status, projectTeamStatusResource), projectLeadStatus);
    }

}
