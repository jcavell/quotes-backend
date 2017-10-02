ALTER TABLE public.quote_line_item DROP CONSTRAINT fk_quote_line_item_quote;
ALTER TABLE public.quote_line_item DROP CONSTRAINT fk_quote_line_item_supplier;
ALTER TABLE public.contact DROP CONSTRAINT fk_contact_supplier;
ALTER TABLE public.contact DROP CONSTRAINT fk_contact_po_address;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_invoice_address;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_delivery_address;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_customer;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_rep;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_assigned_user;
ALTER TABLE public.quote DROP CONSTRAINT fk_quote_enquiry;
ALTER TABLE public.quote_meta DROP CONSTRAINT fk_quote_meta_quote;

ALTER TABLE public.customer DROP CONSTRAINT fk_customer_delivery_address;
ALTER TABLE public.customer DROP CONSTRAINT fk_customer_invoice_address;
ALTER TABLE public.customer DROP CONSTRAINT fk_customer_handler_1;
ALTER TABLE public.customer DROP CONSTRAINT fk_customer_company_1;


DROP TABLE public.quote_line_item;
DROP TABLE public.contact;
DROP TABLE public.quote;
DROP TABLE public.customer;
DROP TABLE public.supplier;
DROP TABLE public.iuser;
DROP TABLE public.enquiry;
DROP TABLE public.company;
DROP TABLE public.address;
DROP TABLE public.xsell;
DROP TABLE public.play_evolutions;
DROP TABLE IF EXISTS QUOTE_META;

DROP SEQUENCE public.address_seq RESTRICT;
DROP SEQUENCE public.company_seq RESTRICT;
DROP SEQUENCE public.contact_seq RESTRICT;
DROP SEQUENCE public.customer_seq RESTRICT;
DROP SEQUENCE public.enquiry_seq RESTRICT;
DROP SEQUENCE public.iuser_seq RESTRICT;
DROP SEQUENCE public.quote_line_item_seq RESTRICT;
DROP SEQUENCE public.quote_seq RESTRICT;
DROP SEQUENCE IF EXISTS public.quote_meta_seq RESTRICT;
DROP SEQUENCE public.supplier_seq RESTRICT;
DROP SEQUENCE public.xsell_seq RESTRICT;

ALTER TABLE public.asi_quote_product DROP CONSTRAINT fk_asi_quote_product_quote;
ALTER TABLE public.asi_quote_product DROP CONSTRAINT fk_asi_quote_product_product;
ALTER TABLE public.asi_quote DROP CONSTRAINT fk_asi_quote_customer_1;
DROP TABLE public.asi_quote_product;
DROP TABLE public.asi_quote;
DROP TABLE public.asi_product;


DROP SEQUENCE public.asi_product_seq RESTRICT;
DROP SEQUENCE public.asi_quote_product_seq RESTRICT;
DROP SEQUENCE public.asi_quote_seq RESTRICT;

DROP TYPE public.asi_quote_status;