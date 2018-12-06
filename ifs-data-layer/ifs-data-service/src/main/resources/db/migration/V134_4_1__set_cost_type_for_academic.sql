
update finance_row set description = 'YOUR_FINANCE' where name='tsb_reference';
update finance_row set description = 'LABOUR' where name='incurred_staff';
update finance_row set description = 'TRAVEL' where name='incurred_travel_subsistence';
update finance_row set description = 'MATERIALS' where name='incurred_other_costs';
update finance_row set description = 'LABOUR' where name='allocated_investigators';
update finance_row set description = 'OTHER_COSTS' where name='allocated_estates_costs';
update finance_row set description = 'OTHER_COSTS' where name='allocated_other_costs';
update finance_row set description = 'OVERHEADS' where name='indirect_costs';
update finance_row set description = 'LABOUR' where name='exceptions_staff';
update finance_row set description = 'OTHER_COSTS' where name='exceptions_other_costs';