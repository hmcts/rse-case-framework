
with surnames as (
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
                                1,
                                'Mr',
                                forename.column1 as forname,
                                surname.column1 as surname,
                                date(to_timestamp(random() * 2000000001 -1000000001))
from forenames forename, surnames surname, surnames x, surnames y, surnames z;

analyze;
