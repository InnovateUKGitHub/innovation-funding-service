package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;

import java.util.Collection;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 *
 */
public class ServiceResultAwareMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    @Override
    public Object filter(Object filterTarget, Expression filterExpression, EvaluationContext ctx) {

        if (filterTarget != null && ServiceResult.class.isAssignableFrom(filterTarget.getClass())) {

            ServiceResult serviceResult = (ServiceResult) filterTarget;

            if (serviceResult.isFailure()) {
                return serviceResult;
            }

            Object successObject = serviceResult.getSuccessObject();

            if (successObject == null) {
                return serviceResult;
            }

            if (!(Collection.class.isAssignableFrom(successObject.getClass()) || successObject.getClass().isArray())) {
                return serviceResult;
            }

            Object filteredList = super.filter(successObject, filterExpression, ctx);
            return serviceSuccess(filteredList);
        }

        return super.filter(filterTarget, filterExpression, ctx);
    }

    @Override
    public void setReturnObject(Object returnObject, EvaluationContext ctx) {
        if (returnObject != null && ServiceResult.class.isAssignableFrom(returnObject.getClass())) {

            ServiceResult serviceResult = (ServiceResult) returnObject;
            Object successObject = serviceResult.handleSuccessOrFailure(
                    failure -> null,
                    success -> success
            );
            super.setReturnObject(successObject, ctx);
        } else {
            super.setReturnObject(returnObject, ctx);
        }
    }
}
