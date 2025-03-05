package com.example.firstTry.services;

import com.example.firstTry.model.Doctor;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendDoctorApprovalEmail(Doctor doctor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", doctor.getFirstName()+" "+doctor.getLastName());
            context.setVariable("loginUrl", "https://your-app.com/login");

            String htmlContent = templateEngine.process("doctor-approval-email", context);

            helper.setTo(doctor.getEmail());
            helper.setSubject("Your Medical Platform Account Has Been Approved");
            helper.setText(htmlContent, true);

            mailSender.send(message);


                mailSender.send(message);
            } catch (MailAuthenticationException ex) {
                // Handle bad credentials
            throw new MailAuthenticationException("Failed to send approval email", ex);
            } catch (MailSendException | MessagingException exx) {
                // Handle network/configuration issues
            throw new MailSendException("Failed to send approval email", exx);

        }

    }
    public void sendDoctorDeclinedEmail(Doctor doctor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", doctor.getFirstName()+" "+doctor.getLastName());
            context.setVariable("loginUrl", "https://your-app.com/login");

            String htmlContent = templateEngine.process("doctor-declined-email", context);

            helper.setTo(doctor.getEmail());
            helper.setSubject("Your Medical Platform Account Has Been Approved");
            helper.setText(htmlContent, true);

            mailSender.send(message);


            mailSender.send(message);
        } catch (MailAuthenticationException ex) {
            // Handle bad credentials
            throw new MailAuthenticationException("Failed to send approval email", ex);
        } catch (MailSendException | MessagingException exx) {
            // Handle network/configuration issues
            throw new MailSendException("Failed to send approval email", exx);

        }

    }
}
