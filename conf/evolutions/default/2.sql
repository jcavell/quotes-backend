# --- Sample dataset

# --- !Ups


insert into iuser(id, name, email, active) values(1, 'Jonny Cavell', 'jonny.cavell@gmail.com', true);
insert into iuser(id, name, email, mobile_phone, active) values(2, 'Melvin Snedley', 'melvin@gmail.com', '0784593 478734', true);


insert into company (id,name, phone1, source, active) values (  1,'Apple Inc.', '123123123', 'TV', true);
insert into company (id,name, phone1, source, website, active) values (  2,'Orange Inc.', '555555', 'Newspaper', 'https://www.website.com', true);
insert into company (id,name, phone1, phone2, source, active) values (  3,'Pear Inc.', '+44 34098234', 'phone two 777', 'Twitter', true);



insert into supplier(id, name) values (1, 'Supplier 1');
insert into supplier(id, name) values (2, 'Supplier 2');



insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (1, 'Tom Invoice Address', 'Apple', 'Tom', '7 Dunton Close', 'Surbs', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (2, 'Tom Delivery Address', 'Apple', 'Tom', '7 Dunton Close', 'Surbs', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (3, 'Bill Invoice Address', 'Core', 'Bill', '11a Dunton Close', 'Surbiton', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (4, 'Tom Invoice Address', 'Core', 'Bill', '11a Dunton Close', 'Surbs', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (5, 'Tom Invoice Address', 'Bin', 'Bobby', '100 Dunton Close', 'Surbey', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (6, 'Tom Invoice Address', 'Bin', 'Bobby', '100 Dunton Close', 'Surbey', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);



insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (7, 'Mr Super Supplier 1 Address', 'Supplier 1 Inc', 'Mr Super 2', '1001 Dunton Close', 'Surbiton', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (8, 'Mr Super Supplier 2 Address', 'Supplier 2 Inc', 'Mr Super 2', '1001 Dunton Close', 'Surbiton', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);


insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (9, 'Tom Invoice Address for quote', 'Bin', 'Bobby', '100 Dunton Close', 'Surbey', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);

insert into address(id, name, company, fao, line1, line2, townCity, county, postcode, country, active) values (10, 'Tom Delivery Address for quote', 'Bin', 'Bobby', '100 Dunton Close', 'Surbey', 'London', 'Surrey', 'KT6 6QT', 'United Kingdom', true);



insert into contact(id, name, email, direct_phone, is_main_contact, supplier_id, po_address_id) values (1, 'Mr Supplier 1', 'mrsupplier1@gmail.com', '11111', true, 1, 7);

insert into contact(id, name, email, direct_phone, is_main_contact, supplier_id, po_address_id) values (2, 'Mr Supplier 2', 'mrsupplier2@gmail.com', '2222222', true, 2, 8);


insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, invoice_address_id, delivery_address_id, rep_id, company_id, active) values (1,'Tommy', 'Tom Tom','Lord Tomathan Cavell', 'tommy@gmail.com','0123123123123', 'CEO', true, '@jonnycavell', 1, 2, 1, 1, false);
insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, invoice_address_id, delivery_address_id,rep_id, company_id, active) values (2,'Billy', 'Bill Bill','Bill', 'bill@gmail.com','999999999', 'President', true, null, 3, 4, 2, 2, true);
insert into customer (id,first_name, last_name, salutation, email,mobile_phone,position, is_main_contact, twitter, invoice_address_id, delivery_address_id, rep_id, company_id, active) values (3,'Bobby', 'Bob Bob','Mr Bob', 'cleaner@gmail.com','+44 888888 777', 'Cleaner', false, '@cleaner', null, null, 2, 3, true);

insert into quote(id, status, title, date_required, customer_name, customer_email, invoice_address_id, delivery_address_id, customer_id, rep_id, assigned_user_id) values (1, 'new', 'Quote for Tommy', '2017-10-22 23:01:11', 'Bobby Sands', 'bobby@temp.com', 9, 10, 3, 2, 1);

insert into quote_line_item(id, sku, quantity, description, cost, markup, sell, vat, quote_id, supplier_id) values (1, 'SKU101', 500, 'Lovely bottle opener', 12.23, 0.15, 12.60, 0.20, 1, 2);
insert into quote_line_item(id, sku, quantity, description, cost, markup, sell, vat, quote_id, supplier_id) values (2, 'SKU102', 300, 'Lego', 2.23, 0.25, 2.60, 0.20, 1, 1);

insert into xsell(id, product_id) values (1, 550309392);
insert into xsell(id, product_id) values (2, 551177353);





# --- !Downs

delete from xsell;
delete from customer;
delete from address;
delete from iuser;
delete from company;

