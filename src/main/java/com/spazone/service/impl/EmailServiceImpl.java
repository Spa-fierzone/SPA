package com.spazone.service.impl;

import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import com.spazone.exception.InvalidTokenException;
import com.spazone.exception.ResourceNotFoundException;
import com.spazone.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("otpCode", otpCode);

            String htmlContent = templateEngine.process("email/verification-email", context);

            helper.setTo(to);
            helper.setSubject("Your OTP Verification Code");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void sendPasswordResetEmail(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("resetUrl", baseUrl + "/auth/password-reset?token=" + token);

            String htmlContent = templateEngine.process("email/password-reset-email", context);

            helper.setTo(to);
            helper.setSubject("Password Reset Request");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPasswordChangeConfirmation(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your password has been changed");
        message.setText("This is a confirmation that your password has been successfully changed. " +
                "If you did not perform this change, please contact support immediately.");

        mailSender.send(message);
    }

    @Override
    public void sendInvoiceEmail(Invoice invoice) {
        String to = invoice.getCustomer().getEmail();
        String subject = "Hóa đơn thanh toán SpaZone #" + invoice.getInvoiceNumber();
        String body = "Chào " + invoice.getCustomer().getFullName() + ",\n\n"
                + "Cảm ơn bạn đã sử dụng dịch vụ. Dưới đây là thông tin hóa đơn:\n"
                + "Dịch vụ: " + invoice.getAppointment().getService().getName() + "\n"
                + "Tổng cộng: " + invoice.getFinalAmount() + " VNĐ\n"
                + "Phương thức thanh toán: " + invoice.getPaymentMethod() + "Thanh Toán Tiền Mặt\n\n"
                + "Trân trọng,\nSpaZone";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
