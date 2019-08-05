-- Update grant claim percentage to use cost column.

UPDATE finance_row SET cost = quantity WHERE type = 'FINANCE';