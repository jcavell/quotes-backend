# --- First database schema

# --- !Ups

create table company (
  id                        bigint not null,
  name                      varchar(255) not null,
  constraint pk_company primary key (id))
;

create table person (
  id                        bigint not null,
  name                      varchar(255) not null,
  email                 varchar(255) not null,
  tel               varchar(255) not null,
  company_id                bigint,
  constraint pk_person primary key (id))
;

create sequence company_seq start with 1000;

create sequence person_seq start with 1000;

alter table person add constraint fk_person_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_person_company_1 on person (company_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists company;

drop table if exists person;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists company_seq;

drop sequence if exists person_seq;

