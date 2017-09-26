# --- First database schema

# --- !Ups

create sequence iuser_seq start with 1000;
create table iuser (
  id bigint not null default nextval('iuser_seq'),
  name                      varchar(255) not null,
  email                 varchar(255) not null,
  direct_phone                      varchar(255),
  mobile_phone                      varchar(255),
  active boolean default true,
  constraint pk_iuser primary key (id))
;

create index ix_iuser_name on iuser(name);
create index ix_iuser_email on iuser(email);

create sequence address_seq start with 1000;
create table address(
  id bigint not null default nextval('address_seq'),
  name varchar(255) not null,
  company VARCHAR(255) not null,
  fao VARCHAR(255),
  line1 VARCHAR(255) not null,
  line2 VARCHAR(255),
  line3 VARCHAR(255),
  townCity VARCHAR(100) not null,
  county VARCHAR(50),
  postcode VARCHAR(10) not null,
  country VARCHAR(60) not null,
  active boolean default true,
  CONSTRAINT pk_address PRIMARY KEY (id)
);
create index ix_address_postcode on address(postcode);




create sequence supplier_seq start with 1000;

create table supplier(
  id bigint not null default nextval('supplier_seq'),
  name                      varchar(255) not null,
  phone1 VARCHAR(50),
  phone2 VARCHAR(50),
  phone3 VARCHAR(50),
  active boolean default true,
  CONSTRAINT pk_supplier PRIMARY KEY (id)
);
create index ix_supplier_name on supplier(name);


create sequence contact_seq start with 1000;
create table contact (
  id bigint not null default nextval('contact_seq'),
  name                      varchar(255) not null,
  email                 varchar(255) not null,
  direct_phone                      varchar(255),
  mobile_phone                      varchar(255),
  position                      varchar(255),
  is_main_contact BOOLEAN DEFAULT true,
  supplier_id BIGINT,
  active  boolean default true,
  po_address_id BIGINT,
  constraint pk_contact primary key (id))
;

create index ix_contact_name on contact(name);
create index ix_contact_email on contact(email);

alter table contact add constraint fk_contact_supplier_1 foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_contact_supplier_1 on contact (supplier_id);

alter table contact add constraint fk_contact_po_address foreign key (po_address_id) references address (id) on delete restrict on update restrict;



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
  active boolean default true,
  constraint pk_company primary key (id))
;
create index ix_company_name on company(name);



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
  is_main_contact BOOLEAN default true,
  twitter                      varchar(255),
  facebook                      varchar(255),
  linked_in                      varchar(255),
  skype                      varchar(255),
  active  boolean default true,
  delivery_address_id BIGINT,
  invoice_address_id BIGINT,
  rep_id                 bigint,
  company_id                bigint,
  constraint pk_customer primary key (id))
;

create index ix_customer_first_name on customer(first_name);
create index ix_customer_last_name on customer(last_name);
create index ix_customer_salutation on customer(salutation);
create index ix_customer_email on customer(email);

alter table customer add constraint fk_customer_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_customer_company_1 on customer (company_id);

alter table customer add constraint fk_customer_handler_1 foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_customer_handler_1 on customer (rep_id);

alter table customer add constraint fk_customer_delivery_address foreign key (delivery_address_id) references address (id) on delete restrict on update restrict;

alter table customer add constraint fk_customer_invoice_address foreign key (invoice_address_id) references address (id) on delete restrict on update restrict;

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

drop table if exists company;
drop table if exists customer;
drop table if exists enquiry;
drop table if exists xsell;
drop table if exists address;
drop table if exists iuser;

drop sequence if exists enquiry_seq;
drop sequence if exists xsell_seq;
drop sequence if exists company_seq;
drop sequence if exists customer_seq;
drop sequence if exists address_seq;
drop sequence if exists iuser_seq;

SET REFERENTIAL_INTEGRITY TRUE;
