-- Schema changes like this should be in the migration folder rather than the setup folder.
-- However, V41_0__Add_assessment_feedback which was placed in the setup folder by mistake.
-- This schema change needs to exist in the migration folder due to this, since db/setup is always run after db/migration.
-- In the setup folder, the DROP would be attempted prior to the CREATE.
DROP TABLE `assessment_feedback`;