package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class LegacyMonitoringOfficerResourceBuilder extends BaseBuilder<LegacyMonitoringOfficerResource, LegacyMonitoringOfficerResourceBuilder> {


    private LegacyMonitoringOfficerResourceBuilder(List<BiConsumer<Integer, LegacyMonitoringOfficerResource>> multiActions) {
        super(multiActions);
    }

    public static LegacyMonitoringOfficerResourceBuilder newLegacyMonitoringOfficerResource() {
        return new LegacyMonitoringOfficerResourceBuilder(emptyList()).
                with(uniqueIds()).
                withIdBased((id, mo) -> mo.setFirstName("Monitoring " + id)).
                withIdBased((id, mo) -> mo.setLastName("Officer " + id)).
                withIdBased((id, mo) -> mo.setEmail("mo" + id + "@example.com")).
                withIdBased((id, mo) -> mo.setPhoneNumber(id + " 9999"));
    }

    @Override
    protected LegacyMonitoringOfficerResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LegacyMonitoringOfficerResource>> actions) {
        return new LegacyMonitoringOfficerResourceBuilder(actions);
    }

    @Override
    protected LegacyMonitoringOfficerResource createInitial() {
        return new LegacyMonitoringOfficerResource();
    }

    public LegacyMonitoringOfficerResourceBuilder withId(Long id){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setId(id));
    }

    public LegacyMonitoringOfficerResourceBuilder withFirstName(String firstName){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setFirstName(firstName));
    }

    public LegacyMonitoringOfficerResourceBuilder withLastName(String lastName){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setLastName(lastName));
    }

    public LegacyMonitoringOfficerResourceBuilder withEmail(String email){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setEmail(email));
    }

    public LegacyMonitoringOfficerResourceBuilder withPhoneNumber(String phoneNumber){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setPhoneNumber(phoneNumber));
    }

    public LegacyMonitoringOfficerResourceBuilder withProject(Long... project){
        return withArray((projectId, monitoringOfficerResource) -> monitoringOfficerResource.setProject(projectId), project);
    }

}
