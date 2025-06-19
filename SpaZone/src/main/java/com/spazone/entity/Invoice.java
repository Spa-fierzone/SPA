package com.spazone.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;

    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String paymentReference;
    private String paymentChannel;

    @Column(length = 4000)
    private String paymentNotes;

    private String invoiceNumber;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Invoice() {
    }

    public Invoice(Integer invoiceId, Appointment appointment, User customer, Branch branch, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal taxAmount, BigDecimal finalAmount, String paymentStatus, String paymentMethod, LocalDateTime paymentDate, LocalDateTime createdAt, LocalDateTime updatedAt, String paymentReference, String paymentChannel, String paymentNotes, String invoiceNumber) {
        this.invoiceId = invoiceId;
        this.appointment = appointment;
        this.customer = customer;
        this.branch = branch;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentReference = paymentReference;
        this.paymentChannel = paymentChannel;
        this.paymentNotes = paymentNotes;
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public User getCustomer() {
        return customer;
    }

    public Branch getBranch() {
        return branch;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public String getPaymentNotes() {
        return paymentNotes;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public void setPaymentNotes(String paymentNotes) {
        this.paymentNotes = paymentNotes;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}

