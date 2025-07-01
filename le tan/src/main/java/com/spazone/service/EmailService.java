package com.spazone.service;

import com.spazone.entity.Invoice;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
    void sendPasswordResetEmail(String to, String token);
    void sendPasswordChangeConfirmation(String to);

    void sendInvoiceEmail(Invoice invoice);
}