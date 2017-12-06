/**
- Field to be used to store file types as comma separated strings of org.innovateuk.ifs.file.resource.FileTypeCategories.displayName
- Comma separated string is so we can support any number in future.
- NULL for form inputs where appendix is not allowed (false) and those that are not of type org.innovateuk.ifs.form.resource.FormInputType.FILEUPLOAD
- A patch [] has been added in 'setup' directory to supplement this new column which sets this field to allow PDF types for form inputs where appendix is allowed.
**/
ALTER TABLE `form_input` ADD COLUMN `allowed_file_types` LONGTEXT NULL DEFAULT NULL AFTER `active`;