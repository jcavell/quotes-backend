# --- Sample dataset

# --- !Ups

insert into product(id, product_id, name, cost, currency_code) values(1, 550517407, 'product1', 1.123, 'USD');
insert into product(id, product_id, name, cost, currency_code) values(2, 550544575, 'product2', 0.12, 'USD');
insert into product(id, product_id, name, cost, currency_code) values(3, 550517110, 'product3', 2.123, 'USD');
insert into product(id, product_id, name, cost, currency_code) values(4, 550517110, 'product3', 2.523, 'USD');

insert into quote(id, request_timestamp, request_date_required, request_product_id, request_customer_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, person_id) values(1, '2017-08-22 01:02:00', '2017-08-23', 550517407, 'Charles Weasley', 'charlie.weasley@gmail.com', '123123123123', 'Digital Equipment', 550, null, 3);

insert into quote(id, request_timestamp, request_date_required, request_product_id, request_customer_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, person_id) values(2, '2017-08-22 00:04:00', '2017-08-25', 550544575, 'Jonathan Cavell', 'jonny.cavell@gmail.com', '08954 546 989', 'Apple', 550, null, 1);

insert into quote(id, request_timestamp, request_date_required, request_product_id, request_customer_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, person_id) values(3, '2017-08-22 23:01:11', '2017-09-23', 550517110, 'Robert Davro', 'bobby.davro@gmail.com', '08954 546 989', 'Tandy', 550, null, 2);

insert into quote_product(id, quote_id, product_id) values (1, 1, 4);
insert into quote_product(id, quote_id, product_id) values (2, 1, 3);
insert into quote_product(id, quote_id, product_id) values (3, 2, 2);
insert into quote_product(id, quote_id, product_id) values (4, 3, 1);


# --- !Downs

delete from quote;
delete from product;
