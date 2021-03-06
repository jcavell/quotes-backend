# --- Sample dataset

# --- !Ups

insert into asi_product(internal_id, Id, Name, Description, raw_data) values(1, 550517407, 'product1','Lovely product 1', '{"foo" : "bar"}');
insert into asi_product(internal_id, Id, Name, Description, raw_data) values(2, 550544575, 'product2','Lovely product 2', '{"foo" : "bar"}');
insert into asi_product(internal_id, Id, Name, Description, raw_data) values(3, 550517110, 'product3','Lovely product 3', '{"foo" : "bar"}');
insert into asi_product(internal_id, Id, Name, Description, raw_data) values(4, 550517110, 'product3','Lovely product 3', '{"foo" : "bar"}');

insert into asi_quote(id, status, request_timestamp, request_required_date, request_product_id, request_customer_first_name, request_customer_last_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, customer_id) values(1, 'REQUESTED', '2017-08-22 01:02:00', '2017-08-23', 550517407, 'Charles', 'Weasley', 'charlie.weasley@gmail.com', '123123123123', 'Digital Equipment', 550, null, 3);

insert into asi_quote(id, status, request_timestamp, request_required_date, request_product_id, request_customer_first_name, request_customer_last_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, customer_id) values(2, 'REQUESTED', '2017-08-22 00:04:00', '2017-08-25', 550544575, 'Jonathan', 'Cavell', 'jonny.cavell@gmail.com', '08954 546 989', 'Apple', 550, null, 1);

insert into asi_quote(id, status, request_timestamp, request_required_date, request_product_id, request_customer_first_name, request_customer_last_name, request_customer_email, request_customer_tel, request_company, request_quantity, request_other_requirements, customer_id) values(3, 'REQUESTED', '2017-08-22 23:01:11', '2017-09-23', 550517110, 'Robert', 'Davro', 'bobby.davro@gmail.com', '08954 546 989', 'Tandy', 550, null, 2);

insert into asi_quote_product(id, quote_id, product_internal_id) values (1, 1, 4);
insert into asi_quote_product(id, quote_id, product_internal_id) values (2, 1, 3);
insert into asi_quote_product(id, quote_id, product_internal_id) values (3, 2, 2);
insert into asi_quote_product(id, quote_id, product_internal_id) values (4, 3, 1);



# --- !Downs

delete from asi_quote;
delete from asi_product;