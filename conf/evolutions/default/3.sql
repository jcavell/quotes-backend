# --- First database schema

# --- !Ups

create type asi_quote_status as ENUM ('REQUESTED', 'WITH_CUSTOMER', 'WITH_DESIGN', 'WITH_ACCOUNTS');

create sequence asi_quote_seq start with 1000;
create table asi_quote (
  id bigint not null default nextval('quote_seq'),
  status VARCHAR (25)  not null,
  request_timestamp TIMESTAMP not null,
  request_product_id bigint not null,
  request_date_required TIMESTAMP not null,
  request_customer_first_name VARCHAR (500) not null,
  request_customer_last_name VARCHAR (500) not null,
  request_customer_email VARCHAR (500) not null,
  request_customer_tel VARCHAR (255) not null,
  request_company VARCHAR (255) not null,
  request_quantity int not null,
  request_other_requirements VARCHAR (3000),
  customer_id bigint not null,
  constraint pk_asi_quote primary key (id))
;
alter table asi_quote add constraint fk_asi_quote_customer_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_asi_quote_customer_1 on asi_quote (customer_id);
create index ix_asi_quote_status on asi_quote(status);


create sequence asi_product_seq start with 1000;
create table asi_product (
  internal_id bigint not null default nextval('product_seq'),
  raw_data json,
  Id bigint not null,
  Name VARCHAR(20000),
  Description VARCHAR(10000),
  constraint pk_asi_product primary key (internal_id))
;



create sequence quote_product_seq start with 1000;
create table quote_product (
  id bigint not null default nextval('quote_product_seq'),
  quote_id bigint not null,
  product_internal_id bigint not null,
  constraint pk_quote_product primary key (id))
;
alter table asi_quote_product add constraint fk_asi_quote_product_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_asi_quote_product_quote_1 on asi_quote_product (quote_id);
alter table asi_quote_product add constraint fk_asi_quote_product_product foreign key (product_internal_id) references asi_product (internal_id) on delete restrict on update restrict;
create index ix_asi_quote_product_product_1 on asi_quote_product (product_internal_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists asi_quote_product;
drop table if exists asi_quote;
drop table if exists asi_product;


drop type if exists quote_status;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists asi_quote_seq;
drop sequence if exists asi_product_seq;
drop sequence if exists asi_quote_product_seq;