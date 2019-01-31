package org.innovateuk.ifs.project.monitoring.domain;

import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * The role of {@link MonitoringOfficer}.
 */
public enum MonitoringOfficerRole implements ProjectParticipantRole {
    MONITORING_OFFICER(19, "monitoring_officer");

    private final long id;
    private final String name;

    private static final Map<Long, MonitoringOfficerRole> idMap =
            stream(values()).collect(toMap(MonitoringOfficerRole::getId, identity()));

    MonitoringOfficerRole(final long id, final String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public boolean isMonitoringOfficer() {
        return this == MONITORING_OFFICER;
    }

    public static MonitoringOfficerRole getById(Long id) {
        return idMap.get(id);
    }
}