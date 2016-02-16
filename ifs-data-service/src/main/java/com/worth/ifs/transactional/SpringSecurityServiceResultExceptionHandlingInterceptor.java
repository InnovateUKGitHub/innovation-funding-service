package com.worth.ifs.transactional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;

/**
 * This Interceptor operates below Spring Security AOP code that is intercepting ServiceResult-returning Service code,
 * and allows it to catch Spring Security exceptions and convert them into failing ServiceResults.
 */
@Component
public class SpringSecurityServiceResultExceptionHandlingInterceptor implements MethodInterceptor {

    private static final Log LOG = LogFactory.getLog(SpringSecurityServiceResultExceptionHandlingInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (AccessDeniedException | AuthenticationCredentialsNotFoundException e) {
            LOG.warn(e.getClass().getSimpleName() + " caught while processing ServiceResult-returning method.  Converting to a ServiceFailure");
            return serviceFailure(forbiddenError("This action is not permitted."));
        }
    }
}