package org.innovateuk.ifs.setup.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SetupStatusResource {
    private Long id;

    private Boolean completed;

    private String className;

    private Long classPk;

    private Long parentId;

    private Long targetId;

    private String targetClassName;

    public SetupStatusResource() {}

    public SetupStatusResource(String className, Long classPk, String targetClassName, Long targetId) {
        this.className = className;
        this.classPk = classPk;
        this.targetClassName = targetClassName;
        this.targetId = targetId;
    }

    public SetupStatusResource(String className, Long classPk, Long parentId, String targetClassName, Long targetId) {
        this.className = className;
        this.classPk = classPk;
        this.parentId = parentId;
        this.targetClassName = targetClassName;
        this.targetId = targetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getClassPk() {
        return classPk;
    }

    public void setClassPk(Long classPk) {
        this.classPk = classPk;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SetupStatusResource that = (SetupStatusResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(completed, that.completed)
                .append(className, that.className)
                .append(classPk, that.classPk)
                .append(parentId, that.parentId)
                .append(targetId, that.targetId)
                .append(targetClassName, that.targetClassName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(completed)
                .append(className)
                .append(classPk)
                .append(parentId)
                .append(targetId)
                .append(targetClassName)
                .toHashCode();
    }
}
