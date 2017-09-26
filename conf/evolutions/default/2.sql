# --- Sample dataset

# --- !Ups

insert into company (id,name, phone1, source, client_or_supplier, active) values (  1,'Apple Inc.', '123123123', 'TV', 'client', true);
insert into company (id,name, phone1, source, client_or_supplier, website, active) values (  2,'Orange Inc.', '555555', 'Newspaper', 'client', 'https://www.website.com', true);
insert into company (id,name, phone1, phone2, source, client_or_supplier, active) values (  3,'Pear Inc.', '+44 34098234', 'phone two 777', 'Twitter', 'client', true);

insert into iuser(id, name, email, active) values(1, 'Jonny Cavell', 'jonny.cavell@gmail.com', true);
insert into iuser(id, name, email, mobile_phone, active) values(2, 'Melvin Snedley', 'melvin@gmail.com', '0784593 478734', true);

insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, handler_id, company_id) values (1,'Tommy', 'Tom Tom','Lord Tomathan Cavell', 'tommy@gmail.com','0123123123123', 'CEO', true, '@jonnycavell', 1, 1);
insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, handler_id, company_id, active) values (2,'Billy', 'Bill Bill','Bill', 'bill@gmail.com','999999999', 'President', true, null, 2, 2, true);
insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, handler_id, company_id, active) values (3,'Bobby', 'Bob Bob','Mr Bob', 'cleaner@gmail.com','+44 888888 777', 'Cleaner', false, '@cleaner', 2, 3, true);


# --- !Downs

delete from customer;
delete from company;
