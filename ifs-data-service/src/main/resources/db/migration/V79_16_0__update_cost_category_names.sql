
update cost_category set name = 'Investigations' where
name = 'Indirect costs' and label = 'Indirect Costs' ;

update cost_category set label = 'Exceptions' where
name = 'Staff' and label = 'Indirect Costs' ;

update cost_category set label = 'Exceptions' where
name = 'Other costs' and label = 'Indirect costs' ;


update cost_category set name = 'Travel and subsistence' where
name = 'Staff' and label = 'Directly incurred' order by id desc limit 1;

update cost_category set name = 'Investigations' where
name = 'Investigators' and label = 'Directly allocated';