package org.innovateuk.ifs.config;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.engine.HTMLElementDefinition;
import org.thymeleaf.engine.StandardModelFactory;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.*;

import java.util.Stack;

/**
 * Taken from alexuser at http://forum.thymeleaf.org/Thymeleaf-3-enforce-well-formed-HTML-templates-td4029826.html
 */
public class Thymeleaf3ValidHtmlEnforcerPostProcessorHandler extends AbstractTemplateHandler {

    private boolean trimEmptyText = true;
    private boolean removeComments = false;
    private boolean expandNonVoidStandaloneTags = true;

    private StandardModelFactory factory = null;

    private Stack<IOpenElementTag> visitedElementsStack = new Stack<>();

    @Override
    public void setContext(ITemplateContext context) {
        super.setContext(context);
        factory = new StandardModelFactory(context.getConfiguration(), context.getTemplateMode());
    }

    /**
     * Hides comments if removeComments is set to tue
     * @param comment
     */
    @Override
    public void handleComment(IComment comment) {
        if (!removeComments) {
            super.handleComment(comment);
        }
    }

    /**
     * Removes sequences of empty text replacing it with just one space
     * This functionality fits better preprocessor, however when using with layout-dialect
     * sometimes all template model falls apart because some part of code in processor
     * rely on index in model.
     * @param text
     */
    @Override
    public void handleText(IText text) {

        if (trimEmptyText) {

            if (text.getText().equals(" ")) {
                super.handleText(text);
                return;
            }

            String trimmed = text.getText().trim();

            if (!trimmed.isEmpty()) {

                if (text.getText().endsWith(" ")) {
                    trimmed = trimmed + " ";
                }

                if (text.getText().startsWith(" ") || text.getText().startsWith("\n ")) {
                    trimmed = " " + trimmed;
                }

                super.handleText(factory.createText(trimmed));
            }

        } else {
            super.handleText(text);
        }
    }

    @Override
    public void handleOpenElement(IOpenElementTag openElementTag) {
        visitedElementsStack.push(openElementTag);
        super.handleOpenElement(openElementTag);
    }

    @Override
    public void handleCloseElement(ICloseElementTag closeElementTag) {

        IOpenElementTag lastOpenTag = visitedElementsStack.pop();

        if (closeElementTag.isSynthetic() ||
                !lastOpenTag.getElementCompleteName().equals(closeElementTag.getElementCompleteName())) {

            throw new TemplateInputException("Unmatched element found [" +
                    closeElementTag.getElementDefinition().getElementName() +
                    "]",
                    closeElementTag.getTemplateName(), closeElementTag.getLine(), closeElementTag.getCol());
        }
        super.handleCloseElement(closeElementTag);
    }

    /**
     * Method expands tags like
     * <div />
     * to
     * <div></div>
     *
     * Why?
     * To convert xhtml to html5 friendly style.
     * There are cases where standalone divs are used just for layout purpose.
     * Like bootstrap:
     * <div class="col-xs-1" /> <!-- Just for padding, but invalid in HTML5  -->
     * <div class="col-xs-11" >Main content</div>
     * Many browsers can't digest this syntax well because it against html5 spec.
     *
     * This method will ensure that all the non void tags will be expanded to open and closed tags
     *
     * @param tag
     */
    @Override
    public void handleStandaloneElement(IStandaloneElementTag tag) {

        if (expandNonVoidStandaloneTags &&
                tag.getElementDefinition() instanceof HTMLElementDefinition &&
                !((HTMLElementDefinition) tag.getElementDefinition()).getType().isVoid())
        {
            super.handleOpenElement(factory.createOpenElementTag(tag.getElementCompleteName(),
                    tag.getAttributeMap(),
                    null,
                    tag.isSynthetic()));
            super.handleCloseElement(factory.createCloseElementTag(tag.getElementCompleteName(),
                    tag.isSynthetic(),
                    false));
        } else {
            super.handleStandaloneElement(tag);
        }
    }
}
