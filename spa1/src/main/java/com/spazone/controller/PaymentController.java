package com.spazone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spazone.configuration.PayOSConfig;
import com.spazone.dto.PayOSItem;
import com.spazone.dto.PayOSPaymentRequest;
import com.spazone.dto.PayOSPaymentResponse;
import com.spazone.entity.Invoice;
import com.spazone.entity.PaymentTransaction;
import com.spazone.repository.PaymentTransactionRepository;
import com.spazone.service.InvoiceService;
import com.spazone.service.impl.PayOSService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private PayOSService payOSService;

    private static final String baseUrl = "http://localhost:8080"; // Thay đổi theo cấu hình thực tế

    @GetMapping("/payos/{invoiceId}")
    public String createPayOSPayment(@PathVariable Integer invoiceId, Model model) {
        try {
            Invoice invoice = invoiceService.getById(invoiceId);

            // Tạo order code unique
            long orderCode = System.currentTimeMillis();

            // Tạo PayOS payment request
            PayOSPaymentRequest request = new PayOSPaymentRequest();
            request.setOrderCode(orderCode);
            request.setAmount(invoice.getFinalAmount().intValue());
            request.setDescription("Thanh toán hóa đơn #" + invoice.getInvoiceId());

            // Tạo item
            PayOSItem item = new PayOSItem();
            item.setName("Hóa đơn #" + invoice.getInvoiceId());
            item.setQuantity(1);
            item.setPrice(request.getAmount());
            request.setItems(Arrays.asList(item));

            // Set return và cancel URL
            request.setReturnUrl(baseUrl + "/payment/payos-return");
            request.setCancelUrl(baseUrl + "/payment/payos-cancel");

            // Tạo payment link
            PayOSPaymentResponse response = payOSService.createPaymentLink(request);

            if (response.getError() == 0) {
                // Lưu thông tin transaction tạm thời
                PaymentTransaction txn = new PaymentTransaction();
                txn.setInvoice(invoice);
                txn.setAmount(invoice.getFinalAmount());
                txn.setStatus("pending");
                txn.setPaymentMethod("PAYOS");
                txn.setTransactionReference(String.valueOf(orderCode));
                txn.setPaymentDate(LocalDateTime.now());
                txn.setPaymentChannel("ONLINE");
                paymentTransactionRepository.save(txn);

                // Redirect to PayOS checkout
                return "redirect:" + response.getData().getCheckoutUrl();
            } else {
                model.addAttribute("error", "Không thể tạo link thanh toán: " + response.getMessage());
                return "payment/error";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "payment/error";
        }
    }

    @GetMapping("/payos-return")
    public String handlePayOSReturn(@RequestParam Map<String, String> params, Model model) {
        try {
            String orderCode = params.get("orderCode");
            String status = params.get("status");

            if (orderCode != null) {
                // Lấy thông tin payment từ PayOS API để xác thực
                PayOSPaymentResponse paymentInfo = payOSService.getPaymentInfo(Long.parseLong(orderCode));

                // Tìm transaction trong DB
                PaymentTransaction txn = paymentTransactionRepository
                        .findByTransactionReference(orderCode)
                        .orElse(null);

                if (txn != null) {
                    Invoice invoice = txn.getInvoice();

                    if ("PAID".equals(paymentInfo.getData().getStatus())) {
                        // Thanh toán thành công
                        txn.setStatus("success");
                        invoice.setPaymentStatus("paid");
                        invoice.setPaymentDate(LocalDateTime.now());
                        invoice.setPaymentMethod("PAYOS");

                        paymentTransactionRepository.save(txn);
                        invoiceService.save(invoice);

                        model.addAttribute("success", true);
                        model.addAttribute("message", "Thanh toán thành công!");
                    } else {
                        // Thanh toán thất bại
                        txn.setStatus("failed");
                        paymentTransactionRepository.save(txn);

                        model.addAttribute("success", false);
                        model.addAttribute("message", "Thanh toán thất bại!");
                    }

                    model.addAttribute("invoice", invoice);
                    model.addAttribute("transaction", txn);
                }
            }

            return "payment/result";

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            return "payment/error";
        }
    }

    @GetMapping("/payos-cancel")
    public String handlePayOSCancel(@RequestParam Map<String, String> params, Model model) {
        String orderCode = params.get("orderCode");

        if (orderCode != null) {
            // Cập nhật status thành cancelled
            PaymentTransaction txn = paymentTransactionRepository
                    .findByTransactionReference(orderCode)
                    .orElse(null);

            if (txn != null) {
                txn.setStatus("cancelled");
                paymentTransactionRepository.save(txn);

                model.addAttribute("invoice", txn.getInvoice());
                model.addAttribute("message", "Giao dịch đã được hủy!");
            }
        }

        return "payment/cancelled";
    }

    // Webhook để nhận thông báo từ PayOS
    @PostMapping("/payos-webhook")
    @ResponseBody
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody String payload,
                                                     @RequestHeader("x-payos-signature") String signature,
                                                     HttpServletRequest request) {
        try {
            // Xác thực signature
            System.out.println("Received webhook payload: " + payload);
            System.out.println("Received signature: " + signature);
            if (!PayOSConfig.verifyWebhookSignature(signature, payload)) {
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            // Parse webhook data
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> webhookData = mapper.readValue(payload, Map.class);

            String orderCode = webhookData.get("orderCode").toString();
            String status = webhookData.get("status").toString();

            // Tìm và cập nhật transaction
            PaymentTransaction txn = paymentTransactionRepository
                    .findByTransactionReference(orderCode)
                    .orElse(null);

            if (txn != null) {
                Invoice invoice = txn.getInvoice();

                if ("PAID".equals(status)) {
                    txn.setStatus("success");
                    invoice.setPaymentStatus("paid");
                    invoice.setPaymentDate(LocalDateTime.now());
                    invoice.setPaymentMethod("PAYOS");

                    invoiceService.save(invoice);
                } else if ("CANCELLED".equals(status)) {
                    txn.setStatus("cancelled");
                }

                paymentTransactionRepository.save(txn);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing webhook");
        }
    }
}