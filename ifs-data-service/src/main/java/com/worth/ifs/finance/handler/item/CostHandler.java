package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.resource.cost.CostItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CostHandler {
    private static final Log LOG = LogFactory.getLog(CostHandler.class);
    Map<String, CostField> costFields = new HashMap<>();

    public abstract Cost toCost(CostItem costItem);

    public abstract CostItem toCostItem(Cost cost);

    public void validate(@NotNull CostItem costItem, @NotNull BindingResult bindingResult) {
        isValid(bindingResult, costItem, (Class<?>[]) null);
    }

    protected void validate(@NotNull CostItem costItem, @NotNull BindingResult bindingResult, Class<?>... classes) {
        isValid(bindingResult, costItem, classes);
    }

    /**
     * This method is needed because we want to add validator Group to validation.
     * Because we can't use the spring validators for this, we need to convert the validation messages.
     * {@link https://docs.oracle.com/javaee/6/tutorial/doc/gkagv.html}
     */
    private boolean isValid(Errors result, Object o, Class<?>... classes) {
        if (classes == null || classes.length == 0 || classes[0] == null) {
            classes = new Class<?>[]{Default.class};
        }
        javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(o, classes);
        addValidationMessages(result, violations);
        return violations.size() == 0;
    }

    private void addValidationMessages(Errors result, Set<ConstraintViolation<Object>> violations) {
        for (ConstraintViolation<Object> v : violations) {
            Path path = v.getPropertyPath();
            String propertyName = "";
            if (path != null) {
                for (Path.Node n : path) {
                    propertyName += n.getName() + ".";
                }
                propertyName = propertyName.substring(0, propertyName.length() - 1);
            }
            String constraintName = v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
            if (propertyName == null || "".equals(propertyName)) {
                result.reject(constraintName, v.getMessage());
            } else {
                result.rejectValue(propertyName, constraintName, v.getMessage());
            }
        }
    }

    public void setCostFields(List<CostField> costFields) {
        this.costFields = costFields.stream().collect(Collectors.toMap(CostField::getTitle, Function.<CostField>identity()));
        ;
    }

    public List<Cost> initializeCost() {
        ArrayList<Cost> costs = new ArrayList<>();
        return costs;
    }
}
