package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;

import static java.util.Arrays.stream;

/**
 * FormInputType is used to identify what response a FormInput needs.
 * This is also used to choose a template in the web-service. Depending on the FormInputType we
 * can also implement extra behaviour like form / input validation.
 */
public enum FormInputType implements IdentifiableEnum {

    TEXTAREA(2),
    FILEUPLOAD(4),
    ASSESSOR_RESEARCH_CATEGORY(21),
    ASSESSOR_APPLICATION_IN_SCOPE(22),
    ASSESSOR_SCORE(23),
    TEMPLATE_DOCUMENT(29),
    MULTIPLE_CHOICE(30);

    private long id;

    FormInputType(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }

    public static FormInputType findByName(String name) {
        return stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No FormInputType found for name: " + name));
    }

    public static FormInputType findById(long id) {
        return stream(values())
                .filter(e -> e.id == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No FormInputType found for id: " + id));
    }
}