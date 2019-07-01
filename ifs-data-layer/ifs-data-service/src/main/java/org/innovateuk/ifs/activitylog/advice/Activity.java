package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.activitylog.resource.ActivityType;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Activity {

    ActivityType type();

    String condition() default "";

    String applicationId() default "";

    String projectId() default "";

    String projectOrganisationCompositeId() default "";

}
