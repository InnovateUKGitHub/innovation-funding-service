package org.innovateuk.ifs.config;

import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.postprocessor.IPostProcessor;

import java.util.HashSet;
import java.util.Set;

public class IfsThymeleafPostProcessorDialect implements IPostProcessorDialect {

    @Override
    public int getDialectPostProcessorPrecedence() {
        return 0;
    }

    @Override
    public Set<IPostProcessor> getPostProcessors() {
        Set<IPostProcessor> processors = new HashSet<>();
        processors.add(new Thymeleaf3ValidHtmlEnforcerPostProcessor());
        return processors;
    }

    @Override
    public String getName() {
        return "ifspost";
    }
}
