# OutlookMailReading
Reading Mail and Storing into MySql Database, writting into Excel sheet and Reading Excelsheet

#Create Table Script
CREATE TABLE `user_details` (
  `subject`  varchar(100) Not Null,
  `user_id` varchar(50) NOT NULL,
  `location` varchar(50) NULL,
  `ticket_number` varchar(50) null,
  `time` timestamp,
  PRIMARY KEY (`user_id`, `time`)
);
