package org.innovateuk.ifs.competitionsetup.domain;

import org.innovateuk.ifs.file.domain.FileType;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class DocumentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(name = "document_config_file_type",
            joinColumns = @JoinColumn(name = "document_config_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_type_id", referencedColumnName = "id"))
    private List<FileType> fileTypes;

    private String title;

    private String guidance;

    private boolean editable;
    private boolean enabled;

    public DocumentConfig() {
    }

    public DocumentConfig(String title, String guidance, boolean editable, boolean enabled, List<FileType> fileTypes) {
        this.title = title;
        this.guidance = guidance;
        this.editable = editable;
        this.enabled = enabled;
        this.fileTypes = fileTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

