-- we no longer support the custom percentage option that used to exist before the spreadsheet upload.
set @metaFieldId = (select id from finance_row_meta_field where title = 'use_total');

delete from finance_row_meta_value where finance_row_meta_field_id=@metaFieldId;
delete from finance_row_meta_field where id = @metaFieldId
