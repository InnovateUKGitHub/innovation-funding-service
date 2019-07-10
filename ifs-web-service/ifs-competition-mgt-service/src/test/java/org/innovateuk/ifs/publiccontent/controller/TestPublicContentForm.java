package org.innovateuk.ifs.publiccontent.controller;

import org.innovateuk.ifs.management.publiccontent.controller.AbstractPublicContentSectionController;
import org.innovateuk.ifs.management.publiccontent.form.AbstractContentGroupForm;

/**
 * Subclass of {@link AbstractContentGroupForm} used as a type parameter when testing subclasses of
 * {@link AbstractPublicContentSectionController} in {@link AbstractContentGroupControllerTest} and
 * {@link AbstractPublicContentSectionControllerTest}.
 */
class TestPublicContentForm extends AbstractContentGroupForm {
}
