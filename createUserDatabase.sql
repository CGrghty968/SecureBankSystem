CREATE DATABASE ProjectDB;

USE ProjectDB;

# Creating the Table Users to store all information related to the Users of the bank system
CREATE TABLE customers(
	#Store the user's unique account number, this will be assigned upon account creation
    accountNo int primary key auto_increment,
    
	#Store the balance of the user's account, this will begin as 0
    balance decimal(12,2),
    
    #Store the password of the user's account
    password blob,
    
    #Store the salt for the password
    salt blob,
    
    #Store the user's name associated with the account
    accountHolder varchar(20)
);

select * from customers;


SELECT * FROM customers WHERE accountNo=(SELECT MAX(accountNo) FROM customers);

drop table customers

select password AND salt from customers where accountNo = 1;

