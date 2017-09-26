# --- First database schema

# --- !Ups

create sequence enquiry_seq start with 1000;
create table enquiry (
  id bigint NOT NULL default nextval('enquiry_seq'),
  request_id bigint not null,
  request_timestamp TIMESTAMP not null,
  product_id bigint not null,
  date_required TIMESTAMP not null,
  customer_first_name VARCHAR (255) not null,
  customer_last_name VARCHAR (255) not null,
  customer_email VARCHAR (255) not null,
  customer_tel VARCHAR (255) not null,
  company VARCHAR (255) not null,
  quantity int not null,
  other_requirements VARCHAR (255),
  imported BOOLEAN,
  constraint pk_enquiry primary key (id))
;

create sequence xsell_seq start with 1000;
create table xsell (
  id bigint NOT NULL default nextval('xsell_seq'),
  product_id                        bigint not null,
  constraint pk_xsell primary key (id))
;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists enquiry;
drop sequence if exists enquiry_seq;
drop table if exists xsell;
drop sequence if exists xsell_seq;

SET REFERENTIAL_INTEGRITY TRUE;