/**
Column to be used to store file types as org.springframework.http.MediaType comma separated string.
Can be NULL for form inputs that are not of type FILE
**/
ALTER TABLE `ifs`.`form_input` ADD COLUMN `allowed_file_types` LONGTEXT NULL DEFAULT NULL AFTER `active`;