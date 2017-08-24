# --- First database schema

# --- !Ups

create sequence mock_quote_request_seq start with 1000;
create table mock_quote_request (
  id bigint NOT NULL default nextval('mock_quote_request_seq'),
  request_id bigint not null,
  request_timestamp TIMESTAMP not null,
  product_id bigint not null,
  date_required TIMESTAMP not null,
  customer_name VARCHAR (255) not null,
  customer_email VARCHAR (255) not null,
  customer_tel VARCHAR (255) not null,
  company VARCHAR (255) not null,
  quantity int not null,
  other_requirements VARCHAR (255),
  imported BOOLEAN,
  constraint pk_mock_quote_request primary key (id))
;


# --- !Downs

drop table if exists mock_quote_request;

drop sequence if exists mock_quote_request_seq;