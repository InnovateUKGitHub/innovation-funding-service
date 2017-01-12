package org.innovateuk.ifs.file.service;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;

/**
 * A Grant Offer Letter Template Service (a service that can process a template file in order to produce a Grant Offer Letter string) based
 * on Freemarker
 */
@Component
public class FreemarkerGOLTemplateRenderer implements FileTemplateRenderer {

    @Autowired
    private Configuration configuration;
    private static final Log LOG = LogFactory.getLog(FreemarkerGOLTemplateRenderer.class);

    @Override
    public ServiceResult<String> renderTemplate(String templatePath, Map<String, Object> templateReplacements) {

        return handlingErrors(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE), () -> {

            Map<String, Object> replacementsWithCommonObjects = new HashMap<>(templateReplacements);

            try {
                return getStringServiceResult(templatePath, replacementsWithCommonObjects);
            } catch (IOException | TemplateException e) {
                LOG.error("Error rendering GOL template " + templatePath, e);
                return serviceFailure(new Error(GRANT_OFFER_LETTER_GENERATION_UNABLE_TO_RENDER_TEMPLATE));
            }
        });
    }

    private ServiceResult<String> getStringServiceResult(String templatePath, Map<String, Object> replacementsWithCommonObjects) throws IOException, TemplateException {
        Template temp = configuration.getTemplate(templatePath);

        StringWriter writer = new StringWriter();
        preProcessReplacements(replacementsWithCommonObjects);
        temp.process(replacementsWithCommonObjects, writer);
        return serviceSuccess(writer.getBuffer().toString());
    }

    private void preProcessReplacements(final Map<String, Object> replacementsWithCommonObjects) {
        for (Map.Entry<String, Object> entry : replacementsWithCommonObjects.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String valueStr = (String)value;
                entry.setValue(StringEscapeUtils.escapeXml10(valueStr));
            }
        }
    }

}
