package com.spazone.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    private BigDecimal amount;

    private String paymentMethod; // VNPAY, MOMO, etc.
    private String paymentChannel; // ONLINE, COUNTER, etc.
    private String transactionReference;

    private String status; // success / failed
    private LocalDateTime paymentDate;

    @Column(length = 4000)
    private String notes;

    public PaymentTransaction() {
    }

    public PaymentTransaction(Integer transactionId, Invoice invoice, BigDecimal amount, String paymentMethod, String paymentChannel, String transactionReference, String status, LocalDateTime paymentDate, String notes) {
        this.transactionId = transactionId;
        this.invoice = invoice;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentChannel = paymentChannel;
        this.transactionReference = transactionReference;
        this.status = status;
        this.paymentDate = paymentDate;
        this.notes = notes;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

