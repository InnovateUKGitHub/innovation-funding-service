package org.innovateuk.ifs.config;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.*;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * this class id responsible for the escaping of <script> tags inside thymeleaf code. it does the same as a th:utext tag except for the fact that it escapes the <script> tags
 */

class EnhancedUtextProcessor extends AbstractAttributeTagProcessor {

    public static final int PRECEDENCE = 1400;
    public static final String ATTR_NAME = "utext";
    public static final Whitelist WHITELIST = Whitelist.none()
            // Tags and attributes should be in alphabetical order for ease of lookup
            .addTags(
                    "a", "caption", "h1", "h2", "h3", "div", "em", "li", "ol", "p", "span",
                    "strong", "table", "tbody", "tfoot", "thead", "td", "th", "tr", "ul", "br"
            )
            .addAttributes("a", "href", "target", "title")
            .addAttributes("div", "class", "id", "aria-hidden")
            .addAttributes("h1", "class", "id")
            .addAttributes("h2", "class", "id")
            .addAttributes("h3", "class", "id")
            .addAttributes("li", "class", "id")
            .addAttributes("ol", "class", "id")
            .addAttributes("table", "summary", "width")
            .addAttributes("td", "colspan", "rowspan", "width")
            .addAttributes("th", "colspan", "rowspan", "scope", "style", "width")
            .addAttributes("ul", "class", "id")

            .preserveRelativeLinks(true)

            .addProtocols("a", "href", "ftp", "http", "https", "mailto");


    public EnhancedUtextProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }


    @Override
    protected void doProcess(
            final ITemplateContext context,
            final IProcessableElementTag tag,
            final AttributeName attributeName,
            final String attributeValue,
            final IElementTagStructureHandler structureHandler) {

        final IEngineConfiguration configuration = context.getConfiguration();

        final IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);

        final Object expressionResult;
        if (expression != null && expression instanceof FragmentExpression) {
            // This is merely a FragmentExpression (not complex, not combined with anything), so we can apply a shortcut
            // so that we don't require a "null" result for this expression if the template does not exist. That will
            // save a call to resource.exists() which might be costly.

            final FragmentExpression.ExecutedFragmentExpression executedFragmentExpression =
                    FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expression, StandardExpressionExecutionContext.RESTRICTED);

            expressionResult =
                    FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);

        } else if(expression != null) {
            expressionResult = expression.execute(context, StandardExpressionExecutionContext.RESTRICTED);
        } else {
            expressionResult = null;
        }


        // If result is no-op, there's nothing to execute
        if (expressionResult == NoOpToken.VALUE) {
            return;
        }


        /*
         * First of all we should check whether the expression result is a Fragment so that, in such case, we can
         * avoid creating a String in memory for it and just append its model.
         */
        if (expressionResult != null && expressionResult instanceof Fragment) {
            if (expressionResult == Fragment.EMPTY_FRAGMENT) {
                structureHandler.removeBody();
                return;
            }
            structureHandler.setBody(cleanString(((Fragment)expressionResult).getTemplateModel()), false);
            return;
        }


        final String unescapedTextStr = (expressionResult == null ? "" : expressionResult.toString());

        /*
         * We will check if there are configured post processors or not. The reason we do this is because output
         * inserted as a result of a th:utext attribute, even if it might be markup, will never be considered as
         * 'processable', i.e. no other processors/inliner will ever be able to act on it. The main reason for this
         * is to protect against code injection.
         *
         * So the only other agents that would be able to modify these th:utext results are POST-PROCESSORS. And
         * they will indeed need markup to have been parsed in order to separate text from structures, so that's why
         * we check if there actually are any post-processors and, if not (most common case), simply output the
         * expression result as if it were a mere (unescaped) text node.
         */
        final Set<IPostProcessor> postProcessors = configuration.getPostProcessors(getTemplateMode());
        if (postProcessors.isEmpty()) {
            structureHandler.setBody(cleanString(unescapedTextStr), false);
            return;
        }


        /*
         * We have post-processors, so from here one we will have to decide whether we need to parse the unescaped
         * text or not...
         */
        if (!mightContainStructures(unescapedTextStr)) {
            // If this text contains no markup structures, there would be no need to parse it or treat it as markup!
            structureHandler.setBody(cleanString(unescapedTextStr), false);
            return;
        }


        /*
         * We have post-processors AND this text might contain structures, so there is no alternative but parsing
         */
        final TemplateModel parsedFragment =
                configuration.getTemplateManager().parseString(
                        context.getTemplateData(),
                        unescapedTextStr,
                        0, 0, // we won't apply offset here because the inserted text does not really come from the template itself
                        null, // No template mode forcing required
                        false); // useCache == false because we could potentially pollute the cache with too many entries (th:utext is too variable!)

        // Setting 'processable' to false avoiding text inliners processing already generated text,
        // which in turn avoids code injection.
        structureHandler.setBody(cleanString(parsedFragment), false);
    }

    private String cleanString(Object result) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String baseUri = request.getScheme() + "://" + request.getServerName();

        return Jsoup.clean((result == null ? "" : result.toString()), baseUri, WHITELIST);
    }

    /*
     * This method will be used for determining if we actually need to apply a parser to the unescaped text that we
     * are going to use a a result of this th:utext execution. If there is no '>' character in it, then it is
     * nothing but a piece of text, and applying the parser would be overkill
     */
    private static boolean mightContainStructures(final CharSequence unescapedText) {
        int n = unescapedText.length();
        char c;
        while (n-- != 0) {
            c = unescapedText.charAt(n);
            if (c == '>' || c == ']') {
                // Might be the end of a structure!
                return true;
            }
        }
        return false;
    }
}
