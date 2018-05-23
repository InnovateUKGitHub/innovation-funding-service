package org.innovateuk.ifs.util;

import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.reject;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

public final class ValidationUtil {

    private ValidationUtil() {}

    /**
     * This method is needed because we want to add validator Group to validation.
     * Because we can't use the spring validators for this, we need to convert the validation messages.
     * {@link http://docs.oracle.com/javaee/6/tutorial/doc/gkagv.html}
     */
    public static boolean isValid(Errors result, Object o, Class<?>... classes) {
        if (classes == null || classes.length == 0 || classes[0] == null) {
            classes = new Class<?>[]{Default.class};
        }
        javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(o, classes);
        addValidationMessages(result, violations);
        return violations.size() == 0;
    }

    public static void addValidationMessages(Errors result, Set<ConstraintViolation<Object>> violations) {
        for (ConstraintViolation<Object> v : violations) {
            Path path = v.getPropertyPath();
            String propertyName = "";
            if (path != null) {
                for (Path.Node n : path) {
                    propertyName += n.getName() + ".";
                }
                propertyName = propertyName.substring(0, propertyName.length() - 1);
            }

            Map<String, Object> attributes = v.getConstraintDescriptor().getAttributes();
            Map<String, Object> messageArguments =
                    attributes != null ?
                            simpleFilter(attributes, (key, value) -> !asList("groups", "message", "payload", "inclusive").contains(key))
                            : emptyMap();

            List<Object> messageArgumentValues = new ArrayList<>(messageArguments.values());

            if (propertyName == null || "".equals(propertyName)) {
                reject(result, v.getMessage(), messageArgumentValues.toArray());
            } else {
                rejectValue(result, propertyName, v.getMessage(), messageArgumentValues.toArray());
            }
        }
    }
}
