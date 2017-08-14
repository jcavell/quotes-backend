# --- First database schema

# --- !Ups


create table quote_request (
  id                        bigint not null,
  quote_timestamp TIMESTAMP not null,
  quantity int not null,
  date_required TIMESTAMP not null,
  other_requirements VARCHAR (255),
  person_id bigint not null,
  quote_request_product_id int not null,
  constraint pk_quote_request primary key (id))
;


alter table quote_request add constraint fk_quote_request_person_1 foreign key (person_id) references person (id) on delete restrict on update restrict;
create index ix_quote_request_person_1 on quote_request (person_id);

create table quote_request_product (
  id                        bigint not null,
  product_id                        bigint not null,
  name VARCHAR(255) not null,
  cost DECIMAL(20, 3) not null,
  currency_code varchar(10) not null,
  constraint pk_quote_request_product primary key (id))
;

alter table quote_request add constraint fk_quote_request_quote_request_product_1 foreign key (quote_request_product_id) references quote_request_product (id) on delete restrict on update restrict;
create index ix_quote_request_quote_request_product_1 on quote_request (quote_request_product_id);


create sequence quote_request_seq start with 1000;

create sequence quote_request_product_seq start with 1000;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists quote_request;
drop table if exists quote_request_product;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists quote_request_seq;
drop sequence if exists quote_request_product_seq;
