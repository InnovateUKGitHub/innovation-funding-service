package org.innovateuk.ifs;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Optional;
import java.util.function.Consumer;

import static org.springframework.test.util.ReflectionTestUtils.getField;

/**
 * A set of functions that can be used by Builders to set fields.
 */
public class ResourceBuilderAmendFunctions extends BaseBuilderAmendFunctions {

    public static <T> Consumer<T> userResource(UserResource user) {
        return t -> setUserResource(user, t);
    }

    public static <T> Consumer<T> competitionResource(CompetitionResource competition) {
        return t -> setCompetitionResource(competition, t);
    }

    public static <T> Consumer<T> competition(Long competition) {
        return t -> setCompetition(competition, t);
    }

    public static <T> Consumer<T> applicationResource(ApplicationResource application) {
        return t -> setApplicationResource(application, t);
    }

    public static Optional<CompetitionResource> getCompetitionResource(Object object) {
        return Optional.ofNullable((CompetitionResource) getField(object, "competition"));
    }

    public static <T> T setApplicationResource(ApplicationResource value, T instance) {
        return setField("application", value.getId(), instance);
    }

    public static <T> T setCompetitionResource(CompetitionResource value, T instance) {
        return setField("competition", value.getId(), instance);
    }

    public static <T> T setCompetition(Long value, T instance) {
        return setField("competition", value, instance);
    }

    public static <T> T setUserResource(UserResource value, T instance) {
        return setField("user", value.getId(), instance);
    }
}
