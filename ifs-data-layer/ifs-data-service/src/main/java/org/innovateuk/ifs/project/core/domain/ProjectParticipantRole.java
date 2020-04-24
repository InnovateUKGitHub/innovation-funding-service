package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.invite.domain.ParticipantRole;

import java.util.EnumSet;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * The role of {@link ProjectUser}.
 */
public enum ProjectParticipantRole implements ParticipantRole {
    PROJECT_PARTNER(10, "partner"),
    PROJECT_MANAGER(11, "project_manager"),
    PROJECT_FINANCE_CONTACT(9, "finance_contact"),
    MONITORING_OFFICER(19, "monitoring_officer"),
    FINANCE_REVIEWER(20, "finance_reviewer"),
    GRANTS_PROJECT_MANAGER(22, "grants_project_manager"),
    GRANTS_PROJECT_FINANCE_CONTACT(23, "grants_finance_contact"),
    GRANTS_MONITORING_OFFICER(24, "grants_monitoring_officer");

    public static final EnumSet<ProjectParticipantRole> PROJECT_USER_ROLES =
            EnumSet.of(PROJECT_PARTNER, PROJECT_MANAGER, PROJECT_FINANCE_CONTACT, GRANTS_PROJECT_MANAGER, GRANTS_PROJECT_FINANCE_CONTACT);

    public static final EnumSet<ProjectParticipantRole> DISPLAY_PROJECT_TEAM_ROLES = EnumSet.of(PROJECT_PARTNER, PROJECT_MANAGER, PROJECT_FINANCE_CONTACT);

    public static final EnumSet<ProjectParticipantRole> PROJECT_MONITORING_OFFICER_ROLES =
            EnumSet.of(MONITORING_OFFICER, GRANTS_MONITORING_OFFICER);

    private final long id;
    private final String name;

    private static final Map<Long, ProjectParticipantRole> idMap =
            stream(values()).collect(toMap(ProjectParticipantRole::getId, identity()));

    ProjectParticipantRole(final long id, final String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public boolean isPartner() {
        return this == PROJECT_PARTNER;
    }

    public boolean isProjectManager() {
        return this == PROJECT_MANAGER;
    }

    public boolean isFinanceContact() {
        return this == PROJECT_FINANCE_CONTACT;
    }

    public boolean isMonitoringOfficer() {
        return this == MONITORING_OFFICER;
    }

    public static ProjectParticipantRole getById(Long id) {
        return idMap.get(id);
    }
}