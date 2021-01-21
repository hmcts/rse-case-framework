
with case_ids as (
    select max(case_id) as case_id from cases
),
surnames as (
    values
    ('Smith'),
    ('Brown'),
    ('Wilson'),
    ('Stewart'),
    ('Campbell'),
    ('Thomson'),
    ('Robertson'),
    ('Anderson'),
    ('Taylor'),
    ('Murray'),
    ('Macdonald'),
    ('Scott')
),
     forenames as (
         values
         ('Rory'),
         ('Alexander'),
         ('Max'),
         ('Logan'),
         ('Lucas'),
         ('Harry'),
         ('Theo'),
         ('Thomas'),
         ('Brodie'),
         ('Archie'),
         ('Jacob'),
         ('Finlay'),
         ('Finn'),
         ('Daniel'),
         ('Joshua'),
         ('Oscar'),
         ('Arthur')
     )
insert into citizen select
                        case_ids.case_id,
                        'Mr',
                        forename.column1 as forname,
                        surname.column1 as surname,
                        date(to_timestamp(random() * 2000000001 -1000000001)),
                        case when round(random()*100) + 1 between 0 and 75 then 'Active'
                             else 'Inactive'
                            end
from case_ids, forenames forename, surnames surname, surnames x, surnames y, surnames z
limit 120000;

analyze;
