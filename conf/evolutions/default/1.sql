# --- First database schema

# --- !Ups

create TYPE pay_status as ENUM ('UNPAID', 'PART_PAID', 'PAID');
create TYPE quote_status as ENUM ('NEW');
create TYPE quote_stage as ENUM ('ENQUIRY', 'QUOTE', 'SALES', 'INVOICE');

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
  name                      varchar(255) not null UNIQUE ,
  canonical_name                      varchar(255) not null UNIQUE,
  phone1 VARCHAR(50),
  canonical_phone1 VARCHAR(50),
  phone2 VARCHAR(50),
  canonical_phone2 VARCHAR(50),
  phone3 VARCHAR(50),
  canonical_phone3 VARCHAR(50),
  website VARCHAR(255),
  twitter VARCHAR(255),
  facebook VARCHAR(255),
  linked_in VARCHAR(255),
  source VARCHAR(255),
  active boolean default true,
  constraint pk_company primary key (id))
;
create index ix_company_name on company(name);
create index ix_company_canonical_name on company(canonical_name);


create sequence customer_seq start with 1000;
create table customer (
  id bigint not null default nextval('customer_seq'),
  name                      varchar(255) not null,
  canonical_name                      varchar(255) not null,
  email                 varchar(255) not null,
  direct_phone                      varchar(255),
  mobile_phone                      varchar(255),
  canonical_mobile_phone                      varchar(255),
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

create index ix_customer_name on customer(name);
create index ix_customer_email on customer(email);
create index ix_customer_canonical_name on customer(canonical_name);
create index ix_customer_mobile_phone on customer(mobile_phone);
create index ix_customer_canonical_mobile_phone on customer(canonical_mobile_phone);

alter table customer add constraint fk_customer_company_1 foreign key (company_id) references company (id) on delete restrict on update restrict;
create index ix_customer_company_1 on customer (company_id);

alter table customer add constraint fk_customer_handler_1 foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_customer_handler_1 on customer (rep_id);

alter table customer add constraint fk_customer_delivery_address foreign key (delivery_address_id) references address (id) on delete restrict on update restrict;

alter table customer add constraint fk_customer_invoice_address foreign key (invoice_address_id) references address (id) on delete restrict on update restrict;

create sequence mock_enquiry_seq start with 1000;
create table mock_enquiry (
  id bigint NOT NULL default nextval('mock_enquiry_seq'),
  enquiry_id bigint not null,
  enquiry_timestamp TIMESTAMP not null,
  product_id BIGINT not null,
  sku varchar(200) not null,
  product_name VARCHAR (255) not null,
  supplier VARCHAR (255),
  colour VARCHAR (255),
  customer_name VARCHAR (255) not null,
  customer_email VARCHAR (255) not null,
  customer_telephone VARCHAR (255) not null,
  company VARCHAR (255) not null,
  required_date TIMESTAMP not null,
  quantity int not null,
  rep_id int not null,
  rep_email VARCHAR (255) not null,
  source VARCHAR (255),
  subject VARCHAR (255),
  xsell_product_ids BIGINT[],
  other_requirements VARCHAR (255),
  imported BOOLEAN,
  constraint pk_mock_enquiry primary key (id))
;


create sequence enquiry_seq start with 1000;
create table enquiry (
  id bigint NOT NULL default nextval('enquiry_seq'),
  enquiry_id bigint not null,
  enquiry_timestamp TIMESTAMP not null,
  product_id BIGINT not null,
  sku varchar(200) not null,
  product_name VARCHAR (255) not null,
  supplier VARCHAR (255),
  colour VARCHAR (255),
  customer_name VARCHAR (255) not null,
  customer_email VARCHAR (255) not null,
  customer_telephone VARCHAR (255) not null,
  company VARCHAR (255) not null,
  required_date TIMESTAMP not null,
  quantity int not null,
  rep_id int not null,
  rep_email VARCHAR (255) not null,
  source VARCHAR (255),
  subject VARCHAR (255) not null,
  xsell_product_ids BIGINT[],
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
  required_date TIMESTAMP NOT NULL,
  notes VARCHAR(2000),
  special_instructions VARCHAR(1000),
  customer_id BIGINT,
  rep_email VARCHAR(200) NOT NULL,
  rep_id BIGINT,
  enquiry_id BIGINT,
  active BOOLEAN default true,
  constraint pk_quote primary key (id)
);

alter table quote add constraint fk_quote_customer foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_quote_customer_1 on quote (customer_id);

alter table quote add constraint fk_quote_rep foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_quote_rep_1 on quote (rep_id);

alter table quote add constraint fk_quote_enquiry foreign key (enquiry_id) references enquiry (id) on delete restrict on update restrict;
create index ix_quote_enquiry_1 on quote (enquiry_id);


create SEQUENCE quote_meta_seq start with 1000;
create table quote_meta(
  id bigint NOT NULL default nextval('quote_meta_seq'),
  status quote_status default 'NEW',
  stage quote_stage default 'QUOTE',
  quote_loss_reason VARCHAR(50),
  quote_sent_date TIMESTAMP,
  sale_sent_date TIMESTAMP,
  invoice_sent_date TIMESTAMP,
  payment_terms VARCHAR(100),
  payment_due_date TIMESTAMP,
  assigned_group varchar(20),
  assigned_user_id BIGINT,
  quote_id BIGINT,
  payment_status pay_status default 'UNPAID',
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
  rep_id BIGINT,
  supplier_id BIGINT NOT NULL,
  po_address_id BIGINT,
  active BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT pk_contact PRIMARY KEY (id)
);
alter table contact add constraint fk_contact_supplier foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_contact_supplier_1 on contact (supplier_id);

alter table contact add constraint fk_contact_po_address foreign key (po_address_id) references address (id) on delete restrict on update restrict;
create index ix_contact_po_address_1 on contact (po_address_id);

alter table contact add constraint fk_contact_rep foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_contact_rep_1 on contact (rep_id);

create index ix_name on contact(name);
create index ix_email on contact(email);


create table product(
  product_id BIGINT UNIQUE NOT NULL,
  name VARCHAR(200) NOT NULL,
  active BOOLEAN DEFAULT true,
  CONSTRAINT  pk_product PRIMARY KEY (product_id)
);

create SEQUENCE quote_line_item_seq start with 1000;
create table quote_line_item (
  id BIGINT NOT NULL DEFAULT nextval('quote_line_item_seq'),
  product_id BIGINT NOT NULL ,
  quantity INT NOT NULL,
  colour VARCHAR(30),
  description VARCHAR(255),
  price_includes VARCHAR(255),
  cost NUMERIC(8,3),
  markup NUMERIC(3,3),
  sell NUMERIC(8,3),
  vat NUMERIC(3,3),
  quote_id BIGINT NOT NULL ,
  supplier_id BIGINT,
  CONSTRAINT pk_quote_line_item PRIMARY KEY (id)
);
alter table quote_line_item add constraint fk_quote_line_item_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_quote_line_item_quote_1 on quote_line_item (quote_id);

alter table quote_line_item add constraint fk_quote_line_item_supplier foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_quote_line_item_supplier_1 on quote_line_item (supplier_id);

-- alter table quote_line_item add constraint fk_quote_line_item_product foreign key (product_id) references product (product_id) on delete restrict on update restrict;
-- create index ix_quote_line_item_product_id_1 on quote_line_item (product_id);


create SEQUENCE quote_xsell_item_seq start with 1000;
create table quote_xsell_item (
  id BIGINT NOT NULL DEFAULT nextval('quote_xsell_item_seq'),
  product_id BIGINT NOT NULL ,
  quote_id BIGINT NOT NULL ,
  CONSTRAINT pk_quote_xsell_item PRIMARY KEY (id)
);
alter table quote_xsell_item add constraint fk_quote_xsell_item_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_quote_xsell_item_quote_1 on quote_xsell_item (quote_id);

-- alter table quote_xsell_item add constraint fk_quote_xsell_item_product foreign key (product_id) references product (product_id) on delete restrict on update restrict;
-- create index ix_quote_xsell_item_product_id_1 on quote_xsell_item (product_id);


create SEQUENCE po_seq start with 1000;
create table po (
  id BIGINT NOT NULL DEFAULT nextval('po_seq'),
  created_date TIMESTAMP NOT NULL DEFAULT now(),
  purchase_title VARCHAR(200),
  supplier_reference VARCHAR(50),
  required_date TIMESTAMP NOT NULL ,
  po_sent_date TIMESTAMP,
  invoice_received BOOLEAN NOT NULL DEFAULT false,
  supplier_address_id BIGINT NOT NULL,
  delivery_address_id BIGINT NOT NULL ,
  quote_id BIGINT NOT NULL,
  supplier_id BIGINT NOT NULL,
  contact_id BIGINT NOT NULL,
  rep_id BIGINT NOT NULL,
  notes VARCHAR(10000),
  active BOOLEAN NOT NULL DEFAULT true,
  CONSTRAINT pk_po PRIMARY KEY (id)
);

alter table po add constraint fk_po_supplier_address foreign key (supplier_address_id) references address (id) on delete restrict on update restrict;
create index ix_po_supplier_address_1 on po (supplier_address_id);

alter table po add constraint fk_po_delivery_address foreign key (delivery_address_id) references address (id) on delete restrict on update restrict;
create index ix_po_delivery_address_1 on po (delivery_address_id);

alter table po add constraint fk_po_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_po_quote_1 on po (quote_id);

alter table po add constraint fk_po_rep foreign key (rep_id) references iuser (id) on delete restrict on update restrict;
create index ix_po_rep_1 on po (rep_id);

alter table po add constraint fk_po_supplier foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_po_supplier_1 on po (supplier_id);

alter table po add constraint fk_po_contact foreign key (contact_id) references contact (id) on delete restrict on update restrict;
create index ix_po_contact_1 on po (contact_id);



create SEQUENCE po_line_item_seq start with 1000;
create table po_line_item (
  id BIGINT NOT NULL DEFAULT nextval('po_line_item_seq'),
  product_id BIGINT NOT NULL ,
  quantity INT NOT NULL,
  colour VARCHAR(30),
  description VARCHAR(255),
  price_includes VARCHAR(255),
  cost NUMERIC(5,3),
  vat NUMERIC(2,1),
  quote_line_item_id BIGINT NOT NULL ,
  po_id BIGINT NOT NULL,
  CONSTRAINT pk_po_line_item PRIMARY KEY (id)
);
alter table po_line_item add constraint fk_po_line_item_quote_line_item foreign key (quote_line_item_id) references quote_line_item (id) on delete restrict on update restrict;
create index ix_po_line_item_quote_line_item_1 on po_line_item (quote_line_item_id);

alter table po_line_item add constraint fk_po_line_item_po foreign key (po_id) references po (id) on delete restrict on update restrict;
create index ix_po_line_item_po_id_1 on po_line_item (po_id);

create index ix_po_line_item_product_id_1 on po_line_item (product_id);



create SEQUENCE payment_seq start with 1000;
create table payment (
  id BIGINT NOT NULL DEFAULT nextval('payment_seq'),
  quote_id BIGINT NOT NULL ,
  amount NUMERIC(5,3) NOT NULL ,
  payment_ref VARCHAR(200) NOT NULL ,
  payment_type VARCHAR(200) NOT NULL ,
  payment_date TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT pk_payment PRIMARY KEY (id)
);
alter table payment add constraint fk_payment_quote foreign key (quote_id) references quote (id) on delete restrict on update restrict;
create index ix_payment_quote_1 on payment (quote_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists quote;
drop table if exists quote_meta;
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
drop sequence if exists quote__meta_seq;
drop sequence if exists enquiry_seq;
drop sequence if exists xsell_seq;
drop sequence if exists company_seq;
drop sequence if exists customer_seq;
drop sequence if exists address_seq;
drop sequence if exists iuser_seq;
drop sequence if exists contact_seq;


SET REFERENTIAL_INTEGRITY TRUE;
