-- IFS-3916 - Configurable Project Setup documents
-- Add a new table project_document

CREATE TABLE `project_document` (
    `id`                    bigint(20)          NOT NULL AUTO_INCREMENT,
	`competition_id`        bigint(20)          NOT NULL,
	`title`                 varchar(255)        NOT NULL,
	`guidance`              varchar(5000)       NOT NULL,
	`editable`              bit(1)              NOT NULL DEFAULT false,
	`enabled`               bit(1)              NOT NULL DEFAULT false,
	`pdf`                   bit(1)              NOT NULL DEFAULT false,
	`spreadsheet`           bit(1)              NOT NULL DEFAULT false,
   PRIMARY KEY (`id`),
   CONSTRAINT `project_document_to_competition_fk` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`)
);
