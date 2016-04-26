package com.worth.ifs.config;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;
/**
 * this class is responsible for creating the thymeleaf dialect used to escape <script> tags from otherwise unescaped outputs.
 */

class UtextDialect extends AbstractDialect{
    @Override
    public String getPrefix() {
        return "safe";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<>();
        processors.add(new EnhancedUtextProcessor());
        return processors;
    }
}
