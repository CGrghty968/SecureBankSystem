package ie.atu.BankSystem;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
//Import all relevant modules and packages to enable java to connect to MySql database
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import javax.mail.MessagingException;

// BankSystem class
public class BankSystem {
 // Store the url as a constant (It will never change)
 private static final String URL = "jdbc:mysql://localhost:3306/projectdb"; 

 // This also applies to the user and password
 private static final String USER = "root";
 private static final String PASSWORD = "Ev1lDead10";

 // Create a new Scanner object to allow user to input data
 private static Scanner scanner = new Scanner(System.in);

// Main method
 public static void main(String[] args) {
     // Sample customers
     System.out.println("Welcome to the ATU Bank System");

     // While loop for the menu
     while (true) {
         // Provide user with the ability to create an account, login, or exit
         System.out.println("\n1. Create Account");
         System.out.println("2. Login");
         System.out.println("3. Exit");
         System.out.print("Select an option: ");


//		 Declare the integer choice to store the user's input
         int choice = 0;
         
//         			Input validation - Menu choice
//       If the entered data is an integer
         if(scanner.hasNextInt()) {
//        	 It is safe to store in the choice variable
        	 choice = scanner.nextInt();
         }// End if
         else {
//        	 Else it is not an integer, continue to the next iteration of the loop
        	 scanner.next();
         }// End else
//         			End input validation - Menu choice

         // Switch and case to manage the user input
         switch (choice) {
             case 1: {
                 // the user has chosen to create an account, calling the createAccount method
//                 System.out.println("Create Acc - call you Create Account Method here..");
                 createAccount();
                 break;
             }// End case 1
             case 2: {
                 // User has chosen to login to their account
//                 System.out.println("Login Acc - call you Login Method here..");
                 login();
                 break;
             }// End case 2
             case 3: {
                 // User has chosen to exit the system
                 System.out.println("Thank you for using the ATU Bank System. Goodbye!");
                 return;		
             }// End case 3
             default: 
             // Default case to handle all other inputs (Any inputs other than 1-3 will be invalid)
                 System.out.println("Invalid option. Try again.");
         }// End switch
     }// End while loop
 }// End main

 // this method will create a users account
 public static void createAccount() {
     // At the beginning of the account creation, assign the account number and balance
	 int accountNo = 0;
     double balance = 0.0;
     
//     Empty the Scanner's buffer
     System.out.print(scanner.nextLine());

//					Input validation - User's name
//     Create boolean flag to allow looping for the user to enter a valid name
     boolean correct = false;
     
//     Prompt the user to enter their name
     System.out.print("Enter your full name: ");
     // Capture the user's full name
     String accountHolder = scanner.nextLine();
//     Do-while loop to ensure user confirms to proper input
     do {
//    	 If the entered name does not match the set regular expression
         if(!accountHolder.matches("^[A-Za-z ]{5,20}$")) {
//        	 The user's name is not valid
        	 System.out.println("Error, the name entered is not valid,\nExclude numbers and punctuation.");
//        	 Re-prompt the user to enter their name
             System.out.print("Enter your full name: ");
             // Capture the user's account number
             accountHolder = scanner.nextLine();
         }// End if
         else {
//        	 Set the boolean flag to true, to exit the do-while loop
        	 correct = true;
         }// End else
     }while(!correct); //This loop will only persist as long as the input is not valid
//					End input validation - User's name

//     Reset the boolean flag to false for the use in the password validation
     correct = false;
//					Input validation - User's password
//     Prompt the user to enter their password
     System.out.print("Enter Password: ");
     // Capture the user's password
     String password = scanner.nextLine();

     do {
//    	 Test the entered password against the regular expression
    	 if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,32}$")) {
//    		 The password is weak,
    		 System.out.println("The password entered is weak, try to include:\n"
    		 		+ "At least one lowercase letter\n"
    		 		+ "At least one uppercase letter\n"
    		 		+ "At least one number\n"
    		 		+ "At least 8 characters in length.");
    		 
//    	     Re-prompt the user to enter their password
    	     System.out.print("Enter Password: ");
    	     // Capture the user's password
    	     password = scanner.nextLine();
    	 }// End if
    	 else {
    		 // Else the password is secure enough
    		 correct = true;
    	 }// End else
     }while(!correct); //Loop will again, continue until the input is correct
 
//     Create local variable to store the new password and salt
     byte[] newPassword = null;
     byte[] salt = null;
//   Hash the password now that it is validated
     try {
		salt = PasswordEncryptionService.generateSalt();
		
//		Now encrpyt the password with the salt
		newPassword = PasswordEncryptionService.getEncryptedPassword(password, salt);

	} catch (NoSuchAlgorithmException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvalidKeySpecException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     // try connect to database
     // add the user details
     // catch any exceptions
     try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             ) {

//    	 Statement stmt = conn.createStatement();
//    	 Create the sql String
         String sql = "INSERT INTO customers (password, salt, balance, accountHolder) VALUES (?, ?, ?, ?);";
         
//         Create a PreparedStatement using that SQL String
         PreparedStatement stmt = conn.prepareStatement(sql);
         
//         Set the parameters of the prepared statement
         stmt.setBytes(1, newPassword);
         stmt.setBytes(2, salt);
         stmt.setDouble(3, balance);
         stmt.setString(4, accountHolder);
         
//         Execute the update of the PrepareStament
         stmt.executeUpdate();
         System.out.println("Account successfully created for " + accountHolder);
//       Now that the account was successfully created, access the associated account number for the user
         sql = "SELECT * FROM customers WHERE accountNo=(SELECT MAX(accountNo) FROM customers);";
//       Store that row in a ResultSet
         ResultSet res = stmt.executeQuery(sql);
         if(res.next()) { //If there is data in the result set
//        	 Access the account number from that row
        	 accountNo = res.getInt("accountNo");
         }// End if
         else {
//        	 Else there is an error
        	 System.out.print("Error");
         }// End else
//       Display the account number to the user
         System.out.println("Account number: "+accountNo);
              
//         Catch any exceptions
     } catch (Exception e) {
//    	 Print any exceptions occurred
         e.printStackTrace();
     }// End catch

 }// End createAccount

 // this method will valid the users login
 public static void login() {

     // enter username
     // enter password
     // try connect to database
     // validate the AccountNo and password
	 
     System.out.print("Enter AccountNo: ");
//     String accountNo = scanner.next();
     
//     Declare the integer of accountNo, set to default 0
     int accountNo = 0;
//     If the scanner's next value is an integer
     if(scanner.hasNextInt()) {
//    	 Store that integer in the accountNo variable
    	 accountNo = scanner.nextInt();	
     }// End if
     else {
//    	 Else, if the next value is not an integer, refresh the scanner's buffer
    	 scanner.next();
     }// End else

     System.out.print("Enter Password: ");
     String password = scanner.next();

     try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            ) {

//    	 Statement stmt = conn.createStatement();
//    	 Create a SQL String to pass into the Prepared Statement
    	 String sql = "SELECT password, salt FROM customers WHERE accountNo=?";
    	 
//    	 Create a PreparedStatement and pass in the values
    	 PreparedStatement stmt = conn.prepareStatement(sql);
    	 
//    	 Pass in the values into the PreparedStatement
    	 stmt.setInt(1, accountNo);
    	 
//         String sql = "SELECT * FROM customers WHERE accountNo = '"+accountNo+"' AND password ='"+password+"';";
//         System.out.println(sql);
//    	 Create a ResultSet for the outcome of the query
         ResultSet rs = stmt.executeQuery();

//         [B@76f2bbc1
//         [B@68e965f5
//         If there is values in the ResultSet
         if (rs.next()) {
//        	 Restore the byte array
        	 byte[] data = rs.getBytes("password");
        	 byte[] salt = rs.getBytes("salt");
//        	 Now that we have the byte arrays, we can verify the authenticity of the entered password
        	 if(PasswordEncryptionService.authenticate(password, data, salt)) {
//        		 Multi-factor authentication
        		 scanner.nextLine();
        		 System.out.print("Enter your email:");
        		 String email = scanner.nextLine();
        		 if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
        			 System.out.print("invalid email.");
        		 }
        		 else {
//        			 Try/catch for the email sending
        			 try {
        		            // For Gmail with app-specific password
        		            EmailSender sender = new EmailSender("gealt79@gmail.com", "tfsd cwcv oerk pixc");
        		            
//        		            Get the verification code
        		            String verificationCode = sender.generateVerificationCode();
//        		            Email the verification code to the entered email
        		            sender.sendVerificationEmail(email, verificationCode);
        		            
        		            System.out.println("Verification code sent to  " + email);
        		            
        		            System.out.print("Enter the verification code: ");
        		            String entry = scanner.nextLine();
        		            if(!entry.equals(verificationCode)) {
        		            	System.out.println("Verification code was false!");
        		            }// End if
        		            else {
        		            	System.out.print("Login successful!");
        		            	validCustomer(accountNo);
        		            }// End else
        		            
        		        } catch (MessagingException e) {
        		            System.err.println("Failed to send email: " + e.getMessage());
        		        }// End tryCatch
        			 
        		 }// End else

        	 }// End if
        	 else {
        		 System.out.println("Invalid username or password");
        	 }
         } else {
//        	 Else, the user's credentials are invalid
             System.out.println("Invalid username or password.");
         }// End else

     } catch (Exception e) {
         e.printStackTrace();
     }// End catch
 }// End login

 // this method is called when the login has been successful
 public static void validCustomer(int accountNo) {
//	 Loop to display the menu to the user
     while (true) {
         System.out.println("\n1. Check Balance");
         System.out.println("2. Deposit");
         System.out.println("3. Withdraw");
         System.out.println("4. Logout");
         System.out.print("Select an option: ");

//         Initialise the user's choice variable to 0
         int choice = 0;
         
		//			Input validation - Menu choice
		//If the entered data is an integer
		if(scanner.hasNextInt()) {
		//	 It is safe to store in the choice variable
			 choice = scanner.nextInt();
		}// End if
		else {
		//	 Else it is not an integer, continue to the next iteration of the loop
			 scanner.next();
		}// End else
		//			End input validation - Menu choice

//		Switch and case for the user's selection
         switch (choice) {
             case 1:
                 checkBalance(accountNo);
                 break;
             case 2:
//            	 Prompt the user to enter the amount they wish to deposit
                 System.out.print("Enter deposit amount: ");
//                 If statement to verify that the entered value was a double
                 if(scanner.hasNextDouble()) {
//                	 Store that double in a variable
                	 double depositAmount = scanner.nextDouble();
//                	 Update the balance, with true as boolean to reflect the deposit
                	 updateBalance(accountNo, depositAmount, true);                	 
                 }// End if
                 else {
//                	 Else, the entered value is not a double, inform the user and continue to next iteration of loop
                	 scanner.next();
                	 System.out.println("Invalid entry, input must be numeric float.\nReturning to menu...");
                	 continue;
                 }// End else
                 break;
             case 3:
//            	 Prompt the user to enter the amount they wish to withdraw
                 System.out.print("Enter withdrawal amount: ");
//                 double withdrawAmount = scanner.nextDouble();
//                 updateBalance(accountNo, withdrawAmount, false);
//                 If statement to verify that the entered value is indeed a double
                 if(scanner.hasNextDouble()) {
//                	 Store the amount they wish to withdraw in a variable
                	 double withdrawAmount = scanner.nextDouble();
//                	 Call the updateBalance method with false as boolean to indicate withdraw passing in the amount they wish to withdraw
                	 updateBalance(accountNo, withdrawAmount, false);
                 }// End if
                 else {
//                	 Clear the scanner and display invalid entry info to user
                	 scanner.next();
                	 System.out.println("Invalid entry, input must be numerical float.\nReturning to menu...");
                 }// End else
                 break;
             case 4:
                 System.out.println("Logging out...");
                 return;
             default:
                 System.out.println("Invalid option. Try again.");
         }// End switch-case
     }// End while loop
 }// End validCustomer

// Method to check the balance of the bank account of a user 
 public static void checkBalance(int accountNo) {
     try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             ) {
//		Create SQL String to pass into the PreparedStatement
    	 String sql = "SELECT balance FROM customers WHERE accountNo=?;";
    	 
//    	 Create a PreparedStatement and pass in the SQL String
    	 PreparedStatement stmt = conn.prepareStatement(sql);
    	 
//    	 Pass in the values into the PreparedStatement
    	 stmt.setInt(1, accountNo);
    	 
//         String sql = "SELECT balance FROM customers WHERE accountNo = '"+accountNo+"';";
         System.out.println(sql);
//         Execute the PreparedStatement and store the outcome in a ResultSet
         ResultSet rs = stmt.executeQuery();

//         If there is values in the ResultSet
         if (rs.next()) {
//        	 Print to the console the balance
             System.out.println("Current balance: €" + rs.getDouble("balance"));
         } else {
//        	 Else the user is not found
             System.out.println("User not found.");
         }// End else

//         Catch any exceptions that may occur
     } catch (Exception e) {
         e.printStackTrace();
     }// End catch
 }// End checkBalance

// Method to update the balance of the bank account of a user
 public static void updateBalance(int accountNo, double amount, boolean isDeposit) {
     try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             ) {

         // Check current balance
         double currentBalance = 0;
         
//         Create the SQL String for the PreparedStatement
         String sql = "SELECT balance FROM customers WHERE accountNo=?;";
         
//         Create a PreparedStatement passing the the SQL String
         PreparedStatement stmt = conn.prepareStatement(sql);
         
//         Populate the PreparedStatement with the account number
         stmt.setInt(1, accountNo);
//         Execute the PreparedStatement and store the outcome in a ResultSet
         ResultSet rs = stmt.executeQuery();

//         If there are values in the ResultSet,
         if (rs.next()) {
//        	 Set the current balance to the balance retrieved from the database
             currentBalance = rs.getDouble("balance");
         }// End if
         
         
//         If you try to withdraw more than the current balance
         if (!isDeposit && amount > currentBalance) {
//        	 Inform the user of low funds
             System.out.println("Insufficient funds.");
             return;
         }// End if

//         Create newBalance variable to store the new balance after updating
         double newBalance;
         if (isDeposit) // if true
         {
//        	 If deposit is chosen, add the amount
             newBalance = currentBalance + amount;
         } else { // else withdraw money
             newBalance = currentBalance - amount;
         }// End else

         // Update balance
//         Create a SQL String for the updating of the balance
         String sqlUpdate = "UPDATE customers SET balance=? WHERE accountNo=?;";

//         Create a PreparedStatement passing in the SQL String
         PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
         
//         Set the values of the PreparedStatement
         stmtUpdate.setDouble(1, newBalance);
         stmtUpdate.setInt(2, accountNo);
         stmtUpdate.executeUpdate();
         System.out.println("successful! New balance: €" + newBalance);

//         Catch any exceptions
     } catch (Exception e) {
         e.printStackTrace();
     }// End catch
 }// End updateBalance
}// End class
