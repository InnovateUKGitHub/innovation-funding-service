CREATE TABLE `thread` (
  `id` BIGINT(20) NOT NULL,
  `class_pk` BIGINT(20) NOT NULL,
  `class_name` VARCHAR(255) NOT NULL,
  `thread_type` VARCHAR(45) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `created_on` DATETIME NOT NULL,
  `section` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `post` (
  `id` BIGINT(20) NOT NULL,
  `thread_id` BIGINT(20) NOT NULL,
  `author_id` BIGINT(20) NOT NULL,
  `body` TEXT NOT NULL,
  `created_on` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `post_thread_fk` FOREIGN KEY (`thread_id`) REFERENCES `ifs`.`thread` (`id`),
  CONSTRAINT `post_author_fk` FOREIGN KEY (`author_id`) REFERENCES `ifs`.`user` (`id`));


CREATE TABLE `post_attachment` (
  `post_id` BIGINT(20) NOT NULL,
  `file_entry_id` BIGINT(20) NOT NULL,
  CONSTRAINT post_attachment_pk PRIMARY KEY (`post_id`, `file_entry_id`),
  CONSTRAINT `post_attachment_post_fk` FOREIGN KEY (`post_id`) REFERENCES `ifs`.`post` (`id`),
  CONSTRAINT `post_attachment_file_entry_fk` FOREIGN KEY (`file_entry_id`) REFERENCES `ifs`.`file_entry` (`id`));