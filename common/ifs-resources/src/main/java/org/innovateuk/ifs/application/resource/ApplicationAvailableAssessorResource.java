package org.innovateuk.ifs.application.resource;

/**
 * DTO for an assessor that is either allocatable, or previously allocated to an application.
 */
public class ApplicationAvailableAssessorResource {

    private long userId;
    private String firstName;
    private String lastName;
    private String skillAreas;
    private long totalApplicationsCount;
    private long assignedCount;
    private long submittedCount;

    public enum Sort {
        ASSESSOR("Assessor"),
        SKILL_AREAS("Skill areas"),
        TOTAL_APPLICATIONS("Total applications"),
        ASSIGNED("Assigned"),
        SUBMITTED("Submitted");

        private String columnName;

        Sort(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    private ApplicationAvailableAssessorResource() {}

    public ApplicationAvailableAssessorResource(long userId, String firstName, String lastName, String skillAreas, long totalApplicationsCount, long assignedCount, long submittedCount) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skillAreas = skillAreas;
        this.totalApplicationsCount = totalApplicationsCount;
        this.assignedCount = assignedCount;
        this.submittedCount = submittedCount;
    }

    public long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSkillAreas() {
        return skillAreas;
    }

    public long getTotalApplicationsCount() {
        return totalApplicationsCount;
    }

    public long getAssignedCount() {
        return assignedCount;
    }

    public long getSubmittedCount() {
        return submittedCount;
    }
}