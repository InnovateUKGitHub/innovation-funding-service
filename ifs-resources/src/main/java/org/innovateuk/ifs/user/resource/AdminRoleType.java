package org.innovateuk.ifs.user.resource;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * The AdminRoleType enumeration defines the available admin user roles.
 */
public enum AdminRoleType {

    IFS_ADMINISTRATOR("ifs_administrator", "IFS Administrator"),
    COMP_ADMIN("comp_admin", "Competition Administrator"),
    PROJECT_FINANCE("project_finance", "Project Finance"),
    SUPPORT("support", "IFS Support User")
    ;

    private String name;
    private String displayName;

    AdminRoleType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AdminRoleType fromName(final String name){
        for (final AdminRoleType adminRoleType : AdminRoleType.values()){
            if (adminRoleType.getName().equals(name)){
                return adminRoleType;
            }
        }
        throw new IllegalArgumentException("No UserRoleType with name " + name);
    }

    public static List<String> roleNames(AdminRoleType... roles){
        return asList(roles).stream().map(r -> r.getName()).collect(toList());
    }
}

