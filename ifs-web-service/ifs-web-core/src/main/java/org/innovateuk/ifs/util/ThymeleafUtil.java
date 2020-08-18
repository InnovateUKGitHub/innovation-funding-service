package org.innovateuk.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.config.IfsThymeleafExpressionObjectFactory;
import org.springframework.validation.Errors;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.util.StringFunctions.countWords;

/**
 * This class provides utility methods that can be used in replacement for lengthy OGNL or SpringEL expressions in Thymeleaf.
 * <p>
 * These methods are offered by the #ifsUtil utility object in Thymeleaf variable expressions.
 * <p>
 * #ifsUtil is added to the evaluation context by {@link IfsThymeleafExpressionObjectFactory}.
 */
public class ThymeleafUtil {
    private static final Log LOG = LogFactory.getLog(ThymeleafUtil.class);

    private IExpressionContext context;

    public ThymeleafUtil(IExpressionContext context) {
        this.context = context;
    }

    /**
     * Gets the uri for used for form posts.
     *
     * @param request
     * @return
     */
    public String formPostUri(final HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Cannot determine request URI with query string for null request.");
        }
        LOG.debug("Creating URI for request " + request.getClass() + " with URI " + request.getRequestURI());
        return UriComponentsBuilder.fromPath(request.getServletPath()).build().normalize().toUriString();
    }

    /**
     * <p>
     * Given a maximum word count allowed for an item of content, count the number of words remaining until this limit is reached, for the specified content {@link String}.
     * If the content contains HTML markup then this will first be parsed into a HTML Document and the text content will be extracted.
     * </p>
     *
     * @param maxWordCount
     * @param content
     * @return
     */
    public int wordsRemaining(Integer maxWordCount, String content) {
        return ofNullable(maxWordCount).map(maxWordCountValue -> maxWordCountValue - countWords(content)).orElse(0);
    }

    public long calculatePercentage(long part, long total){
        return Math.round(part * 100.0/total);
    }


    //You can find the form (beanName) from the request context but I can't figure out how atm.
    public boolean hasErrorsStartingWith(String form, String startsWith) {
        final IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        Optional<Errors> maybeErrors = requestContext.getErrors(form);
        if (maybeErrors.isPresent()) {
            Errors errors = maybeErrors.get();
            return errors.getFieldErrors().stream()
                    .anyMatch(error -> error.getField().startsWith(startsWith));
        }
        return false;
    }

}
