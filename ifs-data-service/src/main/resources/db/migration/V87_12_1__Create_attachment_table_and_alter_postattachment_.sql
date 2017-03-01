
CREATE TABLE `attachment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `uploader_id` BIGINT(20) NOT NULL,
  `file_entry_id` BIGINT(20) NOT NULL,
  `created_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `file_entry_UNIQUE` (`file_entry_id` ASC),
  INDEX `attachment_uploader_fk_idx` (`uploader_id` ASC),
  CONSTRAINT `attachment_uploader_fk` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`),
  CONSTRAINT `attachment_fileEntry_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `file_entry` (`id`));


ALTER TABLE `post_attachment` DROP FOREIGN KEY `post_attachment_file_entry_fk`;
ALTER TABLE `post_attachment` CHANGE COLUMN `file_entry_id` `attachment_id` BIGINT(20) NOT NULL ;
ALTER TABLE `post_attachment` ADD CONSTRAINT `post_attachment_attachment_fk` FOREIGN KEY (`attachment_id`) REFERENCES `attachment` (`id`);