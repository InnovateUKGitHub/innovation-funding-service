-- IFS-9645: keep few MO users with static uids
update user set uid = '3036e2d4-dbe1-4d0b-80a2-da841dd1f42c' where email = 'orville.gibbs@gmail.com';
update user set uid = 'ec94e97b-e78e-417a-bf92-34b5d46d43ca' where email = 'nilesh.patti@gmail.com';
update user set uid = '88d183d5-edd9-4c81-9fb0-3ed27745e8ea' where email = 'rupesh.pereira@gmail.com';

-- IFS-9849
update user set uid = '3036e2d4-dbe1-4d0b-80a2-da841dd1f1aa' where email = 'fiona.loughnane@innovateuk.ukri.org';
update user set uid = 'ec94e97b-e78e-417a-bf92-34b5d46d42bb' where email = 'alan.davis@innovateuk.ukri.org';
update user set uid = '88d183d5-edd9-4c81-9fb0-3ed27745e3cc' where email = 'david.pritchard@innovateuk.ukri.org';
update user set uid = '3036e2d4-dbe1-4d0b-80a2-da841dd1f4dd' where email = 'promoak12@gmail.com';
update user set uid = 'ec94e97b-e78e-417a-bf92-34b5d46d45ee' where email = 'ashnachhabra@outlook.com';

-- IFS-10076
update user set uid = 'd1a425ab-7df4-4ba4-a429-3624c385c0c4' where email = 'Alek.Corona@ukri.org';

-- IFS-11464
update user set uid = '26a1c1a7-bfd6-4819-bf0d-dd226d81b05a' where email = 'Matt.Graham@ukri.org';
update user set uid = 'd2dc7de1-aaa4-4186-b65c-e8815f2825d6' where email = 'Aaron.Jennings@ukri.org';
update user set uid = '2984e14f-9bfd-44dd-bfa6-707458b8381e' where email = 'Owen.LloydJones@ukri.org';
update user set uid = '4e7e74f9-eb66-4e9e-afff-60b8923c6674' where email = 'belle.smith@gmail.com';