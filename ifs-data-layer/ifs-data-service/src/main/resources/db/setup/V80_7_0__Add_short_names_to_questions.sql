-- Update question templates so that the new competition created will use these shortnames
 UPDATE question_template SET short_name = 'Je-s Output' where name = 'Upload a pdf copy of the Je-S output confirming a status of ''With Council''';
 UPDATE question_template SET short_name = 'Project finances' where name = 'Provide the project costs for ''{organisationName}''';
 UPDATE question_template SET short_name = 'Indirect costs' where name = 'Overheads';
 UPDATE question_template SET short_name = 'Materials' where name = 'Materials';
 UPDATE question_template SET short_name = 'Capital items' where name = 'Capital Usage';
 UPDATE question_template SET short_name = 'Sub-contracts' where name = 'Sub-contracting costs';
 UPDATE question_template SET short_name = 'Other costs' where name = 'Other costs';
 UPDATE question_template SET short_name = 'Other funding' where name = 'Other funding';
 UPDATE question_template SET short_name = 'Funding level' where name = 'Funding level';
 UPDATE question_template SET short_name = 'Business size' where name = 'Organisation Size';

-- Update existing competition questions with the short names
 UPDATE question SET short_name = 'Je-s Output' where name = 'Upload a pdf copy of the Je-S output confirming a status of ''With Council''';
 UPDATE question SET short_name = 'Project finances' where name = 'Provide the project costs for ''{organisationName}''';
 UPDATE question SET short_name = 'Indirect costs' where name = 'Overheads';
 UPDATE question SET short_name = 'Materials' where name = 'Materials';
 UPDATE question SET short_name = 'Capital items' where name = 'Capital Usage';
 UPDATE question SET short_name = 'Sub-contracts' where name = 'Sub-contracting costs';
 UPDATE question SET short_name = 'Other costs' where name = 'Other costs';
 UPDATE question SET short_name = 'Other funding' where name = 'Other funding';
 UPDATE question SET short_name = 'Funding level' where name = 'Funding level';
 UPDATE question SET short_name = 'Business size' where name = 'Organisation Size';