package ie.atu.BankSystem;

// Import all the relevant packages and modules
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
//	Define the variables for the simple-mail-transfer host, ports and usernames and passwords for the transfer
    private String smtpHost;
    private int smtpPort;
    private String username;
    private String password;
    private boolean useTLS;

    // Constructor with configuration (overloaded)
    public EmailSender(String smtpHost, int smtpPort, String username, String password, boolean useTLS) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.useTLS = useTLS;
    }// End constructor

    // Default constructor for Gmail
    public EmailSender(String username, String password) {
        this("smtp.gmail.com", 587, username, password, true);
    }// End constructor

    // Generate random verification code
    public String generateVerificationCode() {
//    	This generates a random 4 digit code that will be sent to the user to add multi-factor authentication
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }// End generateVerificationCode

    // Send verification email
    public void sendVerificationEmail(String recipientEmail, String verificationCode) throws MessagingException {
//    	Creates a properties object to insert all the smtp values
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

//      Creates a session to use the protocol
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + verificationCode);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new MessagingException("Failed to send email", e);
        }// End tryCatch
    }

    // Helper method for quick sending
    public static void sendGmail(String senderEmail, String appPassword, String recipientEmail) 
            throws MessagingException {
        EmailSender sender = new EmailSender(senderEmail, appPassword);
        String code = sender.generateVerificationCode();
        sender.sendVerificationEmail(recipientEmail, code);
    }// End sendGmail
}// End class