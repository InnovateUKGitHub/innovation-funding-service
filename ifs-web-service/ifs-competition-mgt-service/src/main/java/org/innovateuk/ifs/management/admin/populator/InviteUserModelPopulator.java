package org.innovateuk.ifs.management.admin.populator;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.management.admin.viewmodel.InviteUserViewModel;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class InviteUserModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public InviteUserViewModel populate(InviteUserView type, Set<Role> roles, UserResource loggedInUser) {

        Set<Role> filteredRoles;

        if (loggedInUser.hasAuthority(Authority.SUPER_ADMIN_USER)) {
            filteredRoles = internalRolesExcludingSuperAdminRole(roles);
        } else {
            filteredRoles = internalRolesExcludingAdminAndAuditorRoles(roles);
        }

        InviteUserViewModel inviteUserViewModel =  new InviteUserViewModel(type, filteredRoles);
        inviteUserViewModel.setInnovationSectorOptions(getInnovationSectors());
        return inviteUserViewModel;
    }

    private List<InnovationSectorResource> getInnovationSectors() {
        return categoryRestService.getInnovationSectors().getSuccess();
    }

    public static Set<Role> internalRolesExcludingAdminAndAuditorRoles(Set<Role> roles) {
        return roles.stream()
                .filter(r -> !r.isSuperAdminUser())
                .filter(r -> !r.isIfsAdministrator())
                .filter(r -> !r.isAuditor())
                .collect(toSet());
    }

    public static Set<Role> internalRolesExcludingSuperAdminRole(Set<Role> roles) {
        return roles.stream()
                .filter(r -> !r.isSuperAdminUser())
                .collect(toSet());
    }
}
