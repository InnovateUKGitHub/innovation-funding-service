package org.innovateuk.ifs.management.admin.populator;

import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.management.admin.viewmodel.InviteUserViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InviteUserModelPopulator {

    public InviteUserViewModel populate(InviteUserView type, Set<Role> roles) {
        Set<Role> filteredRoles = roles.stream().filter(r -> !r.isSuperAdminUser()).collect(Collectors.toSet());

        return new InviteUserViewModel(type, filteredRoles);
    }
}
