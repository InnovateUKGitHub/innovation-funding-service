-- IFS-4551
DELETE fir FROM form_input_response AS fir
INNER JOIN form_input fi ON fi.id = fir.form_input_id
WHERE fi.form_input_type_id not in (2, 4, 21, 22, 23, 24, 25, 26, 27, 28, 29);
