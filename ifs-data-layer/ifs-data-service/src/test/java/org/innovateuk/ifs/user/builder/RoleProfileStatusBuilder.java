//package org.innovateuk.ifs.user.builder;
//
//import org.innovateuk.ifs.BaseBuilder;
//import org.innovateuk.ifs.user.domain.Affiliation;
//import org.innovateuk.ifs.user.domain.RoleProfileStatus;
//import org.innovateuk.ifs.user.domain.User;
//import org.innovateuk.ifs.user.resource.Role;
//import org.innovateuk.ifs.user.resource.Title;
//import org.innovateuk.ifs.user.resource.UserStatus;
//
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.Set;
//import java.util.function.BiConsumer;
//
//import static java.util.Collections.emptyList;
//import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
//import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
//
//public class RoleProfileStatusBuilder extends BaseBuilder<RoleProfileStatus, RoleProfileStatusBuilder> {
//
//    private RoleProfileStatusBuilder(List<BiConsumer<Integer, RoleProfileStatus>> multiActions) {
//        super(multiActions);
//    }
//
//    public static RoleProfileStatusBuilder newRoleProfileStatus() {
//        return new RoleProfileStatusBuilder(emptyList()).
//                with(uniqueIds()).
//                withFirstName("User").
//                with(idBasedLastNames()).
//                with(idBasedEmails());
//    }
//
//    @Override
//    protected RoleProfileStatusBuilder createNewBuilderWithActions(List<BiConsumer<Integer, RoleProfileStatus>> actions) {
//        return new RoleProfileStatusBuilder(actions);
//    }
//
//
//    @SafeVarargs
//    public final RoleProfileStatusBuilder withUser(User... users) {
//        return withArray((user, roleProfileStatus) -> setField("user", users, roleProfileStatus), users);
//    }
//
//    @Override
//    protected RoleProfileStatus createInitial() {
//        return new RoleProfileStatus();
//    }
//}
