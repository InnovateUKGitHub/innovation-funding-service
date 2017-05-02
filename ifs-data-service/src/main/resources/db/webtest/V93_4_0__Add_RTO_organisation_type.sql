SELECT @rto_type_id := id FROM organisation_type WHERE name='Research and technology organisations (RTOs)';
UPDATE organisation SET organisation_type_id = @rto_type_id WHERE name='HIVE LIMITED';