-- IFS-3100 Update the data type for cost totals to 6 decimal places
ALTER TABLE cost_total MODIFY total DECIMAL(15,6) DEFAULT '0.000000';