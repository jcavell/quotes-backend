# --- First database schema

# --- !Ups

create sequence company_seq start with 1000;
create table company (
  id bigint not null default nextval('company_seq'),
  name                      varchar(255) not null,
  phone1 VARCHAR(50),
  phone2 VARCHAR(50),
  phone3 VARCHAR(50),
  website VARCHAR(255),
  twitter VARCHAR(255),
  facebook VARCHAR(255),
  linked_in VARCHAR(255),
  source VARCHAR(255),
  client_or_supplier VARCHAR(7),
  active boolean,
  constraint pk_company primary key (id))
;
create index ix_company_name on company(name);


create sequence iuser_seq start with 1000;
create table iuser (
  id bigint not null default nextval('iuser_seq'),
  name                      varchar(255) not null,
  email                 varchar(255) not null,
  direct_phone                      varchar(255),
  mobile_phone                      varchar(255),
  active boolean,
  constraint pk_iuser primary key (id))
;

create index ix_iuser_name on iuser(name);
create index ix_iuser_email on iuser(email);

create sequence customer_seq start with 1000;
create table customer (
  id bigint not null default nextval('customer_seq'),
  first_name                      varchar(255) not null,
  last_name                      varchar(255) not null,
  salutation                      varchar(255),
  email                 varchar(255) not null,
  direct_phone                      varchar(255),
  mobile_phone                      varchar(255),
  source                      varchar(255),
  position                      varchar(255),
  is_main_contact                      BOOLEAN,
  twitter                      varchar(255),
  facebook                      varchar(255),
  linked_in                      varchar(255),
  skype                      varchar(255),
  active  boolean,
  handler_id                 bigint,
  company_id                bigint,
  constraint pk_customer primary key (id))
;

create index ix_customer_first_name on customer(first_name);
create index ix_customer_last_name on customer(last_name);
create index ix_customer_salutation on customer(salutation);
create index ix_customer_email on customer(email);

alter table customer add constraint fk_customer_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_customer_company_1 on customer (company_id);

alter table customer add constraint fk_customer_handler_1 foreign key (handler_id) references iuser (id) on delete restrict on update restrict;
create index ix_customer_handler_1 on customer (handler_id);


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists company;

drop table if exists customer;

drop table if exists iuser;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists company_seq;

drop sequence if exists customer_seq;

drop sequence if exists iuser_seq;
