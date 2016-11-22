package com.worth.ifs;

import com.worth.ifs.base.amend.BaseBuilderAmendFunctions;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.application.domain.Application;

import java.util.*;
import java.util.function.Consumer;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * A set of functions that can be used by Builders to set fields.
 */
public class BuilderAmendFunctions extends BaseBuilderAmendFunctions {

    public static <T> Consumer<T> competition(Competition competition) {
        return t -> setCompetition(competition, t);
    }

    public static <T> Consumer<T> competition(Long competition) {
        return t -> setCompetition(competition, t);
    }

    public static <T> Consumer<T> application(Application application) {
        return t -> setApplication(application, t);
    }

    public static Optional<Competition> getCompetition(Object object) {
        return Optional.ofNullable((Competition) getField(object, "competition"));
    }

    public static <T> T setApplication(Application value, T instance) {
        return setField("application", value, instance);
    }

    public static <T> T setCompetition(Competition value, T instance) {
        return setField("competition", value, instance);
    }

    public static <T> T setCompetition(Long value, T instance) {
        return setField("competition", value, instance);
    }

    public static <T> T setUser(User value, T instance) {
        return setField("user", value, instance);
    }
}
