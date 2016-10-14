package com.worth.ifs.invite.builder;

import java.util.List;
import java.util.function.BiConsumer;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.InviteProjectResource;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;


public class ProjectInviteResourceBuilder  extends BaseBuilder<InviteProjectResource, ProjectInviteResourceBuilder> {

    private ProjectInviteResourceBuilder(List<BiConsumer<Integer, InviteProjectResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectInviteResourceBuilder newInviteProjectResource() {
        return new ProjectInviteResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected InviteProjectResource createInitial() {
        return new InviteProjectResource();
    }

    @Override
    protected ProjectInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, InviteProjectResource>> actions) {
        return new ProjectInviteResourceBuilder(actions);
    }

    public ProjectInviteResourceBuilder withId(Long id) {
        return with((inviteResource) -> inviteResource.setId(id));
    }

    public ProjectInviteResourceBuilder withEmail(final String emailAddress) {
        return with((inviteResource) -> inviteResource.setEmail(emailAddress));
    }

    public ProjectInviteResourceBuilder withName(final String name) {
        return with((inviteResource) -> inviteResource.setName(name));
    }

    public ProjectInviteResourceBuilder withStatus(final InviteStatus status) {
        return with((inviteResource) -> inviteResource.setStatus(status));
    }

    public ProjectInviteResourceBuilder withHash(final String hash) {
        return with((inviteResource) -> inviteResource.setHash(hash));
    }

    public ProjectInviteResourceBuilder withProject(final Long projectId) {
        return with((inviteResource) -> inviteResource.setProject(projectId));
    }

    public  ProjectInviteResourceBuilder withOrganisation(final Long organisationId) {
        return with((inviteResource) -> inviteResource.setOrganisation(organisationId));
    }

    public  ProjectInviteResourceBuilder withLeadOrganisation(final String leadOrganisation) {
        return with((inviteResource) -> inviteResource.setLeadOrganisation(leadOrganisation));
    }

    public  ProjectInviteResourceBuilder withInviteOrganisationName(final String inviteOrganisationName) {
        return with((inviteResource) -> inviteResource.setInviteOrganisationName(inviteOrganisationName));
    }

    public ProjectInviteResourceBuilder withIds(Long... ids) {
        return withArray((id, inviteResource) -> setField("id", id, inviteResource), ids);
    }

    public ProjectInviteResourceBuilder withHash(String... hashes) {
        return withArray((hash, inviteResource) -> setField("hash", hash, inviteResource), hashes);
    }

    public ProjectInviteResourceBuilder withEmails(final String... emailAddresses) {
        return withArray((email, inviteResource) -> setField("email", email, inviteResource), emailAddresses);
    }

    public ProjectInviteResourceBuilder withNames(final String... names) {
        return withArray((name, inviteResource) -> setField("name", name, inviteResource), names);
    }

    public ProjectInviteResourceBuilder withProjectName(final String... names) {
        return withArray((name, inviteResource) -> setField("projectName", name, inviteResource), names);
    }

    public ProjectInviteResourceBuilder withStatuss(final InviteStatus... statusses) {
        return withArray((status, inviteResource) -> setField("status", status, inviteResource), statusses);
    }

    public ProjectInviteResourceBuilder withProjects(final Long... projectIds) {
        return withArray((projectId, inviteResource) -> setField("project", projectId, inviteResource), projectIds);
    }

    public  ProjectInviteResourceBuilder withOrganisations(final Long... organisationIds) {
        return withArray((organisationId, inviteResource) -> setField("organisation", organisationId, inviteResource), organisationIds);
    }

    public ProjectInviteResourceBuilder withApplication(final String... names) {
        return withArray((name, inviteResource) -> setField("name", name, inviteResource), names);
    }

    @Override
    protected void postProcess(int index, InviteProjectResource instance) {
        super.postProcess(index, instance);
    }
}
