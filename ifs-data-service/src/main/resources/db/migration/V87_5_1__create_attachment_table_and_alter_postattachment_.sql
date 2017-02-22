
CREATE TABLE `attachment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `uploader` BIGINT(20) NOT NULL,
  `file_entry` BIGINT(20) NOT NULL,
  `created_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `file_entry_UNIQUE` (`file_entry` ASC),
  INDEX `attachment_uploader_fk_idx` (`uploader` ASC),
  CONSTRAINT `attachment_uploader_fk` FOREIGN KEY (`uploader`) REFERENCES `user` (`id`),
  CONSTRAINT `attachment_fileEntry_fk` FOREIGN KEY (`file_entry`) REFERENCES `file_entry` (`id`));


ALTER TABLE `post_attachment` DROP FOREIGN KEY `post_attachment_file_entry_fk`;
ALTER TABLE `post_attachment` CHANGE COLUMN `file_entry_id` `attachment_id` BIGINT(20) NOT NULL ;
ALTER TABLE `post_attachment` ADD CONSTRAINT `post_attachment_attachment_fk` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`);