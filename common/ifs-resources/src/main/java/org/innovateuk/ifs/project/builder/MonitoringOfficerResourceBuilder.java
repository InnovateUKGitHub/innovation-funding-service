package org.innovateuk.ifs.project.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class MonitoringOfficerResourceBuilder extends BaseBuilder<MonitoringOfficerResource, MonitoringOfficerResourceBuilder> {


    private MonitoringOfficerResourceBuilder(List<BiConsumer<Integer, MonitoringOfficerResource>> multiActions) {
        super(multiActions);
    }

    public static MonitoringOfficerResourceBuilder newMonitoringOfficerResource() {
        return new MonitoringOfficerResourceBuilder(emptyList()).
                with(uniqueIds()).
                withIdBased((id, mo) -> mo.setFirstName("Monitoring " + id)).
                withIdBased((id, mo) -> mo.setLastName("Officer " + id)).
                withIdBased((id, mo) -> mo.setEmail("mo" + id + "@example.com")).
                withIdBased((id, mo) -> mo.setPhoneNumber(id + " 9999"));
    }

    @Override
    protected MonitoringOfficerResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MonitoringOfficerResource>> actions) {
        return new MonitoringOfficerResourceBuilder(actions);
    }

    @Override
    protected MonitoringOfficerResource createInitial() {
        return new MonitoringOfficerResource();
    }

    public MonitoringOfficerResourceBuilder withId(Long id){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setId(id));
    }

    public MonitoringOfficerResourceBuilder withFirstName(String firstName){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setFirstName(firstName));
    }

    public MonitoringOfficerResourceBuilder withLastName(String lastName){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setLastName(lastName));
    }

    public MonitoringOfficerResourceBuilder withEmail(String email){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setEmail(email));
    }

    public MonitoringOfficerResourceBuilder withPhoneNumber(String phoneNumber){
        return with((monitoringOfficerResource) -> monitoringOfficerResource.setPhoneNumber(phoneNumber));
    }

    public MonitoringOfficerResourceBuilder withProject(Long... project){
        return withArray((projectId, monitoringOfficerResource) -> monitoringOfficerResource.setProject(projectId), project);
    }

}
