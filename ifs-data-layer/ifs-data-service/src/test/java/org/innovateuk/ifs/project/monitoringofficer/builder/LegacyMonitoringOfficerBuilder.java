package org.innovateuk.ifs.project.monitoringofficer.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.monitoringofficer.domain.LegacyMonitoringOfficer;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class LegacyMonitoringOfficerBuilder extends BaseBuilder<LegacyMonitoringOfficer, LegacyMonitoringOfficerBuilder> {


    private LegacyMonitoringOfficerBuilder(List<BiConsumer<Integer, LegacyMonitoringOfficer>> multiActions) {
        super(multiActions);
    }

    public static LegacyMonitoringOfficerBuilder newLegacyMonitoringOfficer() {
        return new LegacyMonitoringOfficerBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected LegacyMonitoringOfficerBuilder createNewBuilderWithActions(List<BiConsumer<Integer, LegacyMonitoringOfficer>> actions) {
        return new LegacyMonitoringOfficerBuilder(actions);
    }

    @Override
    protected LegacyMonitoringOfficer createInitial() {
        return new LegacyMonitoringOfficer();
    }

    public LegacyMonitoringOfficerBuilder withId(Long id){
        return with((monitoringOfficer) -> monitoringOfficer.setId(id));
    }

    public LegacyMonitoringOfficerBuilder withFirstName(String firstName){
        return with((monitoringOfficer) -> monitoringOfficer.setFirstName(firstName));
    }

    public LegacyMonitoringOfficerBuilder withLastName(String lastName){
        return with((monitoringOfficer) -> monitoringOfficer.setLastName(lastName));
    }

    public LegacyMonitoringOfficerBuilder withEmail(String email){
        return with((monitoringOfficer) -> monitoringOfficer.setEmail(email));
    }

    public LegacyMonitoringOfficerBuilder withPhoneNumber(String phoneNumber){
        return with((monitoringOfficer) -> monitoringOfficer.setPhoneNumber(phoneNumber));
    }

}
