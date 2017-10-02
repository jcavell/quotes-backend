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
  product_id bigint not null,
  constraint pk_xsell primary key (id))
;



create SEQUENCE quote_seq start with 1000;
create table quote(
  id bigint NOT NULL default nextval('quote_seq'),
  title varchar(150) NOT NULL ,
  created_date TIMESTAMP NOT NULL DEFAULT now(),
  date_required TIMESTAMP NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  customer_email VARCHAR(200) NOT NULL ,
  notes VARCHAR(2000),
  special_instructions VARCHAR(1000),
  invoice_address_id BIGINT,
  delivery_address_id BIGINT,
  customer_id BIGINT NOT NULL ,
  rep_id BIGINT NOT NULL,
  enquiry_id BIGINT,
  active BOOLEAN default true,
  constraint pk_quote primary key (id)
);
alter table quote add constraint fk_quote_invoice_address foreign key (invoice_address_id) references address (id) on delete restrict on update restrict;
create index ix_quote_invoice_address_1 on quote (invoice_address_id);

alter table quote add constraint fk_quote_delivery_address foreign key (delivery_address_id) references address (id) on delete restrict on update restrict;
create index ix_quote_delivery_address_1 on quote (delivery_address_id);

alter table quote add constraint fk_quote_customer foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_quote_customer_1 on quote (customer_id);

alter table quote add constraint fk_quote_rep foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_quote_rep_1 on quote (rep_id);

alter table quote add constraint fk_quote_enquiry foreign key (enquiry_id) references enquiry (id) on delete restrict on update restrict;
create index ix_quote_enquiry_1 on quote (enquiry_id);


create SEQUENCE quote_meta_seq start with 1000;
create table quote_meta(
  id bigint NOT NULL default nextval('quote_meta_seq'),
  status varchar(10) NOT NULL default 'NEW',
  stage varchar(10) NOT NULL default 'QUOTE',
  quote_loss_reason VARCHAR(50),
  quote_sent_date TIMESTAMP,
  sale_sent_date TIMESTAMP,
  invoice_sent_date TIMESTAMP,
  payment_terms VARCHAR(100),
  payment_due_date TIMESTAMP,
  payment_status VARCHAR(20),
  assigned_group varchar(20),
  assigned_user_id BIGINT,
  quote_id BIGINT,
  constraint pk_quote_meta primary key (id)
);
alter table quote_meta add constraint fk_quote_meta_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_quote_meta_quote_id_1 on quote_meta (quote_id);

alter table quote_meta add constraint fk_quote_meta_assigned_user foreign key (assigned_user_id) references iuser (id) on delete restrict on update restrict;
create index ix_quote_meta_assigned_user_1 on quote_meta (assigned_user_id);



create SEQUENCE supplier_seq start with 1000;
create table supplier (
  id BIGINT NOT NULL DEFAULT nextval('supplier_seq'),
  name VARCHAR(255) NOT NULL ,
  active BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT pk_supplier PRIMARY KEY (id)
);
create index ix_supplier_name_1 on supplier (name);

create SEQUENCE contact_seq start with 1000;
create table contact (
  id BIGINT NOT NULL DEFAULT nextval('contact_seq'),
  name VARCHAR(255) NOT NULL ,
  email VARCHAR(255) NOT NULL,
  direct_phone VARCHAR(100),
  mobile_phone VARCHAR(100),
  position VARCHAR(100),
  is_main_contact BOOLEAN NOT NULL DEFAULT true,
  supplier_id BIGINT NOT NULL,
  po_address_id BIGINT,
  active BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT pk_contact PRIMARY KEY (id)
);
alter table contact add constraint fk_contact_supplier foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_contact_supplier_1 on contact (supplier_id);

alter table contact add constraint fk_contact_po_address foreign key (po_address_id) references address (id) on delete restrict on update restrict;
create index ix_contact_po_address_1 on contact (po_address_id);
create index ix_name on contact(name);
create index ix_email on contact(email);

create SEQUENCE quote_line_item_seq start with 1000;
create table quote_line_item (
  id BIGINT NOT NULL DEFAULT nextval('quote_line_item_seq'),
  sku VARCHAR(30) NOT NULL ,
  quantity INT NOT NULL,
  colour VARCHAR(30),
  description VARCHAR(255),
  price_includes VARCHAR(255),
  cost NUMERIC(5,3),
  markup NUMERIC(3,2),
  sell NUMERIC(5,3),
  vat NUMERIC(2,1),
  quote_id BIGINT NOT NULL ,
  supplier_id BIGINT NOT NULL,
  CONSTRAINT pk_quote_line_item PRIMARY KEY (id)
);
alter table quote_line_item add constraint fk_quote_line_item_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_quote_line_item_quote_1 on quote_line_item (quote_id);

alter table quote_line_item add constraint fk_quote_line_item_supplier foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_quote_line_item_supplier_1 on quote_line_item (supplier_id);

create index ix_quote_line_item_sku_1 on quote_line_item (sku);

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists quote;
drop table if exists quote_line_item;
drop table if exists contact;
drop table if exists supplier;
drop table if exists customer;
drop table if exists company;
drop table if exists enquiry;
drop table if exists xsell;
drop table if exists address;
drop table if exists iuser;

drop sequence if exists supplier_seq;
drop SEQUENCE if EXISTS quote_line_item_seq;
drop sequence if exists quote_seq;
drop sequence if exists enquiry_seq;
drop sequence if exists xsell_seq;
drop sequence if exists company_seq;
drop sequence if exists customer_seq;
drop sequence if exists address_seq;
drop sequence if exists iuser_seq;
drop sequence if exists contact_seq;


SET REFERENTIAL_INTEGRITY TRUE;
