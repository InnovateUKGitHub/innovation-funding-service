package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.activitylog.resource.ActivityType;

import java.lang.annotation.*;

/**
 * This annotation should be used on service methods that if successfully called will result in an entry in the
 * {@link org.innovateuk.ifs.activitylog.domain.ActivityLog} table.
 *
 * The service method must return a {@link org.innovateuk.ifs.commons.service.ServiceResult} and only a successful
 * result will create an activity log entry.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Activity {

    /**
     * The type of activity to be created when this service is called.
     */
    ActivityType type();

    /**
     * An optional conditional method that will return a boolean. The result of the method will decide whether or not
     * and activity log entry should be created.
     *
     * The referenced condition method must have the exact same parameters as the annotated method.
     */
    String condition() default "";

    /**
     * The name of the parameter which is the application id.
     */
    String applicationId() default "";

    /**
     * The name of the parameter which is the project id.
     */
    String projectId() default "";

    /**
     * The name of the parameter which is the project and organisation ids.
     */
    String projectOrganisationCompositeId() default "";

}
