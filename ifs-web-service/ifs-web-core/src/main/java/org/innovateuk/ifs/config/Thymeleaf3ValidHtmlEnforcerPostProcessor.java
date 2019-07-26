package org.innovateuk.ifs.config;

import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class Thymeleaf3ValidHtmlEnforcerPostProcessor implements IPostProcessor {

    @Override
    public TemplateMode getTemplateMode() {
        return TemplateMode.HTML;
    }

    @Override
    public int getPrecedence() {
        return 0;
    }

    @Override
    public Class<? extends ITemplateHandler> getHandlerClass() {
        return Thymeleaf3ValidHtmlEnforcerPostProcessorHandler.class;
    }
}
