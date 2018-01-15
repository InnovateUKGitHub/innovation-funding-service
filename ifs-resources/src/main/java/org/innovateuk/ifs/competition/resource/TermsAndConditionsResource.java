package org.innovateuk.ifs.competition.resource;

/**
 * Resource representation of TermsAndConditions
 */
public class TermsAndConditionsResource {

    private Long id;
    private String name;
    private String template;
    private String version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
