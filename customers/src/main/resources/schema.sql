DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
  id INTEGER  PRIMARY KEY,
  username VARCHAR(20),
  name VARCHAR(20),
  surname VARCHAR(40),
  address VARCHAR(250),
  zipcode VARCHAR(10),
  city VARCHAR(40),
  country VARCHAR(40)
);