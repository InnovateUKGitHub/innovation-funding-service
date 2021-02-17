package org.innovateuk.ifs.config.thymeleaf.postprocessor;

import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IOpenElementTag;

import java.util.Stack;

/**
 * Post-processor that ensures that matching start and end tags appear in the correct order on pages prior to
 * outputting the HTML.
 */
public class Thymeleaf3ValidHtmlEnforcerPostProcessorHandler extends AbstractTemplateHandler {

    private Stack<IOpenElementTag> visitedElementsStack = new Stack<>();

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
}
