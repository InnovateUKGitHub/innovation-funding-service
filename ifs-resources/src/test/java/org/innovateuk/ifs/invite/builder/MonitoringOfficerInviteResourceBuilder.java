package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class MonitoringOfficerInviteResourceBuilder extends BaseBuilder<MonitoringOfficerInviteResource, MonitoringOfficerInviteResourceBuilder> {

    private MonitoringOfficerInviteResourceBuilder(final List<BiConsumer<Integer, MonitoringOfficerInviteResource>> newActions) {
        super(newActions);
    }

    @Override
    protected MonitoringOfficerInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MonitoringOfficerInviteResource>> actions) {
        return new MonitoringOfficerInviteResourceBuilder(actions);
    }

    protected MonitoringOfficerInviteResource createInitial() {
        return new MonitoringOfficerInviteResource();
    }

    public static MonitoringOfficerInviteResourceBuilder newMonitoringOfficerInviteResource() {
        return new MonitoringOfficerInviteResourceBuilder(emptyList());
    }

    public MonitoringOfficerInviteResourceBuilder withId(Long... ids) {
        return withArray((id, monitoringOfficerInviteResource) -> monitoringOfficerInviteResource.setId(id), ids);
    }

    public MonitoringOfficerInviteResourceBuilder withHash(String... hashes) {
        return withArray((hash, monitoringOfficerInviteResource) -> monitoringOfficerInviteResource.setHash(hash), hashes);
    }

    public MonitoringOfficerInviteResourceBuilder withEmail(String... emails) {
        return withArray((email, monitoringOfficerInviteResource) -> monitoringOfficerInviteResource.setEmail(email), emails);
    }

    public MonitoringOfficerInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArray((status, monitoringOfficerInviteResource) -> monitoringOfficerInviteResource.setStatus(status), statuses);
    }
}