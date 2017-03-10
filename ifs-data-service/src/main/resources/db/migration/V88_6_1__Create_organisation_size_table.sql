-- Create new organisation size table
CREATE TABLE `organisation_size` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
    )ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- Insert values from the ENUM.
INSERT INTO `organisation_size` (name, description)
    VALUES ('SMALL', 'Micro / small'), ('MEDIUM', 'Medium'), ('LARGE', 'Large');

-- Add the new column reference to application_finance and project_finance.
ALTER TABLE `application_finance`
    ADD COLUMN `organisation_size_id` BIGINT(20) NULL,
    ADD CONSTRAINT `application_finance_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`);

ALTER TABLE `project_finance`
    ADD COLUMN `organisation_size_id` BIGINT(20) NULL,
    ADD CONSTRAINT `project_finance_organisation_size_fk` FOREIGN KEY (`organisation_size_id`) REFERENCES `organisation_size` (`id`);

-- Update the existing finance records to point at the new organisation_size table.
  UPDATE `application_finance` f
  		SET organisation_size_id = (SELECT id FROM organisation_size where name=f.organisation_size);
  UPDATE `project_finance` f
  		SET organisation_size_id = (SELECT id FROM organisation_size where name=f.organisation_size);

-- Remove name column from organisation_size as its no longer required.
ALTER TABLE `organisation_size`
    DROP COLUMN `name`;

-- Remove the old enum columns from tables that are now using organisation_size_id
ALTER TABLE `application_finance`
    DROP COLUMN `organisation_size`;
ALTER TABLE `project_finance`
    DROP COLUMN `organisation_size`;

-- Organisation size is no longer required on the organisation entity.
ALTER TABLE `organisation`
    DROP COLUMN `organisation_size`;