package org.innovateuk.ifs.management.admin.populator;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.management.admin.viewmodel.InviteUserViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InviteUserModelPopulator {

    @Autowired
    private CategoryRestService categoryRestService;

    public InviteUserViewModel populate(InviteUserView type, Set<Role> roles) {
        Set<Role> filteredRoles = roles.stream().filter(r -> !r.isSuperAdminUser()).collect(Collectors.toSet());

        InviteUserViewModel inviteUserViewModel =  new InviteUserViewModel(type, filteredRoles);
        inviteUserViewModel.setInnovationSectorOptions(getInnovationSectors());
        return  inviteUserViewModel;
    }

    private List<InnovationSectorResource> getInnovationSectors() {
        return categoryRestService.getInnovationSectors().getSuccess();
    }

}
