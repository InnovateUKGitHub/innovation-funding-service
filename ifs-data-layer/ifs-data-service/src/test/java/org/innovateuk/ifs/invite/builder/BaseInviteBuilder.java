package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.ProcessActivity;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class BaseInviteBuilder<V extends ProcessActivity, T extends Invite<V, T>, S extends BaseInviteBuilder> extends BaseBuilder<T, S> {

    public BaseInviteBuilder(List<BiConsumer<Integer, T>> multiActions) {
        super(multiActions);
    }

    public S withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    protected S withTarget(V... targets) {
        return withArray((target, invite) -> invite.setTarget(target), targets);
    }

    public S withName(String... names) {
        return withArray((name, invite) -> invite.setName(name), names);
    }

    public S withEmail(String... emails) {
        return withArray((email, invite) -> invite.setEmail(email), emails);
    }

    public S withUser(User... users) {
        return withArray((user, invite) -> invite.setUser(user), users);
    }

    public S withUser(Builder<User, ?> users) {
        return withUser(users.build());
    }

    public S withHash(String... hashes) {
        return withArray((hash, invite) -> invite.setHash(hash), hashes);
    }

    public S withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public S withSentBy(User... users) { return withArraySetFieldByReflection("sentBy", users);}

    public S withSentOn(ZonedDateTime... zonedDateTimes) { return withArraySetFieldByReflection("sentOn", zonedDateTimes);}
}