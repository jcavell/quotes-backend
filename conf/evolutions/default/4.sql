# --- Sample dataset

# --- !Ups

insert into quote_request_product(id, product_id, name, cost, currency_code) values(1, 550517407, 'product1', 1.123, 'USD');
insert into quote_request_product(id, product_id, name, cost, currency_code) values(2, 550544575, 'product2', 0.12, 'USD');
insert into quote_request_product(id, product_id, name, cost, currency_code) values(3, 550517110, 'product3', 2.123, 'USD');

insert into quote_request (id, person_id, quote_request_product_id, quote_timestamp, other_requirements, date_required, quantity) values
  (1, 3, 2, '2017-08-22 00:00:00', 'Make it look nice', '2017-08-23', 50);
insert into quote_request (id, person_id, quote_request_product_id, quote_timestamp, other_requirements, date_required, quantity) values
  (2, 1, 3, '2017-08-22 00:00:00', 'Make it look nice', '2017-08-28', 5000);
  insert into quote_request (id, person_id, quote_request_product_id, quote_timestamp, other_requirements, date_required, quantity) values
  (3, 2, 1, '2017-08-22 00:00:00', 'Make it look nice', '2017-09-23', 500);


# --- !Downs

delete from quote_request;
delete from quote_request_product;
