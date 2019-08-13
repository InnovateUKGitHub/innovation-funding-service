-- IFS-6331 remove finance row types from template competitions

delete from competition_finance_row_types where competition_id in (1,2,3,4,5,6,7,8,9);
