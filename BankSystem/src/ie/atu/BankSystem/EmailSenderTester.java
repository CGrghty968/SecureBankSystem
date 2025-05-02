package ie.atu.BankSystem;

import javax.mail.MessagingException;

public class EmailSenderTester {
	    public static void main(String[] args) {
	        try {
	            // For Gmail with app-specific password
	            EmailSender sender = new EmailSender("gealt79@gmail.com", "tfsd cwcv oerk pixc");
	            
	            String verificationCode = sender.generateVerificationCode();
	            sender.sendVerificationEmail("cillinater@gmail.com", verificationCode);
	            
	            System.out.println("Verification code sent: " + verificationCode);
	        } catch (MessagingException e) {
	            System.err.println("Failed to send email: " + e.getMessage());
	        }
	    }
}
