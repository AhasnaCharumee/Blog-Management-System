package lk.ijse.gdse72.blog_management.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLoginSuccessEmail(String toEmail, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to BlogSphere! 🎉 You’ve successfully logged in — now your ideas are just waiting to be shared. Go ahead, start writing your next great blog post and let your voice be heard!");
        message.setText("Hello " + name + ",\n\nYou have successfully logged into the system.\n\nBest Regards,\nYour App Team");
        mailSender.send(message);
    }
}
