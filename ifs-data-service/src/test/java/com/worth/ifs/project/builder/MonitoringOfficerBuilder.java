package com.worth.ifs.project.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.project.domain.MonitoringOfficer;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class MonitoringOfficerBuilder extends BaseBuilder<MonitoringOfficer, MonitoringOfficerBuilder> {


    private MonitoringOfficerBuilder(List<BiConsumer<Integer, MonitoringOfficer>> multiActions) {
        super(multiActions);
    }

    public static MonitoringOfficerBuilder newMonitoringOfficer() {
        return new MonitoringOfficerBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected MonitoringOfficerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, MonitoringOfficer>> actions) {
        return new MonitoringOfficerBuilder(actions);
    }

    @Override
    protected MonitoringOfficer createInitial() {
        return new MonitoringOfficer();
    }

    public MonitoringOfficerBuilder withId(Long id){
        return with((monitoringOfficer) -> monitoringOfficer.setId(id));
    }

    public MonitoringOfficerBuilder withFirstName(String firstName){
        return with((monitoringOfficer) -> monitoringOfficer.setFirstName(firstName));
    }

    public MonitoringOfficerBuilder withLastName(String lastName){
        return with((monitoringOfficer) -> monitoringOfficer.setLastName(lastName));
    }

    public MonitoringOfficerBuilder withEmail(String email){
        return with((monitoringOfficer) -> monitoringOfficer.setEmail(email));
    }

    public MonitoringOfficerBuilder withPhoneNumber(String phoneNumber){
        return with((monitoringOfficer) -> monitoringOfficer.setPhoneNumber(phoneNumber));
    }

}
