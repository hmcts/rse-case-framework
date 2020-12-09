create table cases(
  case_id bigserial not null primary key
);

CREATE TYPE case_state AS ENUM ('Created', 'Stayed', 'Closed');

create type event as enum (
    'CreateClaim',
    'AddParty',
    'AddClaim',
    'AddNotes',
    'CloseCase',
    'ImportCitizens',
    'PurgeInactiveCitizens',
    'SubmitAppeal'
    );

create table users(
    user_id varchar primary key,
    user_forename varchar not null,
    user_surname varchar not null
);

create table events(
  case_id bigint references cases(case_id) not null,
  id event not null,
  sequence_number serial not null unique,
  timestamp timestamp not null default now(),
  state case_state not null,
  user_id varchar not null references users(user_id),
  unique(case_id, sequence_number)
);

create table unspec_cases(
    case_id bigint references cases(case_id) not null primary key,
    data jsonb not null
);

CREATE TYPE claim_state AS ENUM ('Issued', 'Stayed', 'ServiceConfirmed');

create table claims(
    claim_id bigserial not null primary key,
    case_id bigint not null references cases(case_id),
    lower_amount bigint,
    higher_amount bigint
);

CREATE TYPE claim_event AS ENUM (
    'ClaimIssued',
    'ConfirmService',
    'ServiceAcknowledged',
    'ResponseFiled'
);

create table claim_events(
  claim_id bigint not null references claims(claim_id),
  user_id varchar not null references users(user_id),
  id claim_event not null,
  state claim_state not null,
  sequence_number serial not null unique,
  timestamp timestamp default now()
);

-- View for claims with their current states.
create view claims_with_states as
with latest_events as (
    select claim_id, max(claim_events.sequence_number) as latest_seq
    from claims
             join claim_events using (claim_id)
    group by claim_id
)
select
    claims.*,
    claim_events.state
from latest_events
    join claims using (claim_id)
    join claim_events on claim_events.claim_id = latest_events.claim_id
        and claim_events.sequence_number = latest_seq;

create table parties(
                        party_id bigserial not null primary key,
                        case_id bigint references cases(case_id) not null,
                        data jsonb not null
);

CREATE TYPE party_type AS ENUM ('claimant', 'defendant');

create table claim_parties(
    claim_id bigint not null references claims(claim_id),
    party_id bigint not null references parties(party_id),
    party_type party_type not null,
    -- A party can only be on one side of a claim
    unique (claim_id, party_id)
);

create table citizen(
    case_id bigint not null references cases(case_id),
    title varchar,
    forename varchar,
    surname varchar,
    date_of_birth date,
        status varchar not null
);

-- View for cases with their current states.
create view cases_with_states as
with latest_events as (
    select case_id, max(events.sequence_number) as latest_seq
    from unspec_cases
             join events using (case_id)
    group by case_id
)
select
    latest_events.case_id,
    events.state
from latest_events
         join events on events.case_id = latest_events.case_id
    and  events.sequence_number = latest_seq;

-- View of claims with their associated parties
create view claims_with_parties as
with parties_by_type as (
    select claim_id,
           case
               when party_type = 'defendant' then 'defendants'
               when party_type = 'claimant' then 'claimants'
               end as party_type,
           array_agg(data) as parties
    from claim_parties
             join parties using(party_id)
    group by claim_id, party_type
)
select claim_id, json_object_agg(party_type, parties) as parties
from parties_by_type
group by claim_id;

create view case_history as
select case_id, user_id, id::text, timestamp from events
union all
select case_id, user_id, id::text, timestamp from claim_events
join claims using (claim_id);
