# --- First database schema

# --- !Ups

create type quote_status as ENUM ('REQUESTED', 'WITH_CUSTOMER', 'WITH_DESIGN', 'WITH_ACCOUNTS');

create table quote (
  id                        bigint not null,
  status quote_status not null,
  request_timestamp TIMESTAMP not null,
  request_product_id bigint not null,
  request_date_required TIMESTAMP not null,
  request_customer_name VARCHAR (255) not null,
  request_customer_email VARCHAR (255) not null,
  request_customer_tel VARCHAR (255) not null,
  request_company VARCHAR (255) not null,
  request_quantity int not null,
  request_other_requirements VARCHAR (255),
  person_id bigint not null,
  constraint pk_quote primary key (id))
;


alter table quote add constraint fk_quote_person_1 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_quote_person_1 on quote (person_id);

create table product (
  id                        bigint not null,
  product_id                        bigint not null,
  name VARCHAR(255) not null,
  cost DECIMAL(20, 3) not null,
  currency_code varchar(10) not null,
  data json,
  constraint pk_product primary key (id))
;


create table quote_product (
  id                        bigint not null,
  quote_id                        bigint not null,
  product_id                        bigint not null,
  constraint pk_quote_product primary key (id))
;
alter table quote_product add constraint fk_quote_product_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_quote_product_quote_1 on quote_product (quote_id);
alter table quote_product add constraint fk_quote_product_product foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_quote_product_product_1 on quote_product (product_id);


create sequence quote_seq start with 1000;

create sequence product_seq start with 1000;

create sequence quote_product_seq start with 1000;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists quote_product;
drop table if exists quote;
drop table if exists product;

drop type if exists quote_status;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists quote_seq;
drop sequence if exists product_seq;
drop sequence if exists quote_product_seq;
