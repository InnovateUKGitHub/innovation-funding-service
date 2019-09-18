-- IFS 6090

-- Add finance row type to finance_row;

ALTER TABLE finance_row ADD COLUMN type enum (
    'LABOUR',
    'OVERHEADS',
    'PROCUREMENT_OVERHEADS',
    'MATERIALS',
    'CAPITAL_USAGE',
    'SUBCONTRACTING_COSTS',
    'TRAVEL',
    'OTHER_COSTS',
    'FINANCE',
    'OTHER_FUNDING',
    'YOUR_FINANCE',
    'VAT'
);

update finance_row fr
      inner join question q
          on q.id = fr.question_id
      join form_input fi
          on q.id = fi.question_id
      inner join form_input_type fit
          on fit.id = fi.form_input_type_id
      set type = fit.name
      where fit.name in (
          'LABOUR',
          'OVERHEADS',
          'PROCUREMENT_OVERHEADS',
          'MATERIALS',
          'CAPITAL_USAGE',
          'TRAVEL',
          'OTHER_COSTS',
          'FINANCE',
          'OTHER_FUNDING',
          'YOUR_FINANCE',
          'VAT'
  );

update finance_row fr
  inner join question q
      on q.id = fr.question_id
  join form_input fi
      on q.id = fi.question_id
  inner join form_input_type fit
      on fit.id = fi.form_input_type_id
  set type = 'SUBCONTRACTING_COSTS'
  where fit.name = 'SUBCONTRACTING'
