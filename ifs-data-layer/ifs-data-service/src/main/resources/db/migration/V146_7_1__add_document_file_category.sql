-- IFS-6181 Adding document file type

ALTER TABLE appendix_file_types MODIFY type ENUM(
    'PDF',
    'SPREADSHEET',
    'DOCUMENT'
);