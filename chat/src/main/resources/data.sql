CREATE TABLE message (
  id          bigint(20) PRIMARY KEY,
  customer_id varchar(255) NOT NULL,
  from_service_provider bit(1)  NOT NULL,
  service_id varchar(255) NOT NULL,
  message_body varchar(5000) CHARACTER SET utf8  NOT NULL,
  time  datetime, NOT NULL);
  
  CREATE TABLE comment (
  id          bigint(20) PRIMARY KEY,
  customer_id varchar(255) NOT NULL,
  service_id varchar(255) NOT NULL,
  comment_body varchar(5000) CHARACTER SET utf8  NOT NULL,
  rating_double,
  time  datetime, NOT NULL);
  

  