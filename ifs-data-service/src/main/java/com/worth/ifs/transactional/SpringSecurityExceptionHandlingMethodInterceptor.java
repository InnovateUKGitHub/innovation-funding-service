package com.worth.ifs.transactional;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.error.Errors.forbiddenError;

@Component
public class SpringSecurityExceptionHandlingMethodInterceptor implements MethodInterceptor {

    private static final Log LOG = LogFactory.getLog(SpringSecurityExceptionHandlingMethodInterceptor.class);

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