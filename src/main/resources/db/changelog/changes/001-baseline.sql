--liquibase formatted sql

--changeset aldhafara:001-baseline
--comment Baseline current PostgreSQL schema

create table if not exists public.families (
    update_date timestamp(6) with time zone,
    add_by uuid,
    father_id uuid,
    id uuid not null,
    mother_id uuid,
    version bigint,
    constraint families_pkey primary key (id)
);

create table if not exists public.persons (
    birth_date timestamp(6) with time zone,
    update_date timestamp(6) with time zone,
    add_by uuid,
    family_id uuid,
    id uuid not null,
    birth_place character varying(255),
    family_name character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    sex character varying(255),
    constraint persons_sex_check check (sex in ('MALE', 'FEMALE')),
    constraint persons_pkey primary key (id)
);

create table if not exists public.register_users (
    details_id uuid,
    id uuid not null,
    login character varying(255),
    password character varying(255),
    roles character varying(255),
    constraint register_users_pkey primary key (id)
);

create index if not exists idx_family_id on public.persons using btree (family_id);
create index if not exists idx_father_id on public.families using btree (father_id);
create index if not exists idx_mother_id on public.families using btree (mother_id);

alter table if exists public.families
    add constraint fk_families_mother foreign key (mother_id) references public.persons(id);

alter table if exists public.families
    add constraint fk_families_father foreign key (father_id) references public.persons(id);

alter table if exists public.persons
    add constraint fk_persons_family foreign key (family_id) references public.families(id);

--rollback not required
