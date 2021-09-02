package org.innovateuk.ifs.testdata.builders;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Generates data for Monitoring officer on the platform
 */
public class MonitoringOfficerDataBuilder extends BaseDataBuilder<Void, MonitoringOfficerDataBuilder> {

    public static MonitoringOfficerDataBuilder newMonitoringOfficerData(ServiceLocator serviceLocator) {

        return new MonitoringOfficerDataBuilder(emptyList(), serviceLocator);
    }

    private MonitoringOfficerDataBuilder(List<BiConsumer<Integer, Void>> multiActions,
                                         ServiceLocator serviceLocator) {
        super(multiActions, serviceLocator);

    }

    @Override
    protected MonitoringOfficerDataBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Void>> actions) {
        return new MonitoringOfficerDataBuilder(actions, serviceLocator);
    }

    @Override
    protected Void createInitial() {
        return null;
    }
}
