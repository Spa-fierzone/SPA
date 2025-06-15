package com.spazone.controller;

import com.spazone.configuration.VNPayConfig;
import com.spazone.entity.Invoice;
import com.spazone.entity.PaymentTransaction;
import com.spazone.repository.PaymentTransactionRepository;
import com.spazone.service.InvoiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @GetMapping("/vnpay/{invoiceId}")
    public String createVnPayUrl(@PathVariable Integer invoiceId, HttpServletRequest req) {
        Invoice invoice = invoiceService.getById(invoiceId);
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_OrderInfo = "INV:" + invoice.getInvoiceId();
        String vnp_Amount = String.valueOf(invoice.getFinalAmount().multiply(BigDecimal.valueOf(100)).longValue());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnpParams.put("vnp_Amount", vnp_Amount);
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);
        vnpParams.put("vnp_OrderInfo", vnp_OrderInfo);
        vnpParams.put("vnp_OrderType", "billpayment");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnpParams.put("vnp_IpAddr", req.getRemoteAddr());
        vnpParams.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String name : fieldNames) {
            String value = vnpParams.get(name);
            if (hashData.length() > 0) hashData.append("&");

            // Encode value for hashData using VNPay's standard
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8).replace("%20", "+");
            hashData.append(name).append("=").append(encodedValue);

            // Standard encoding for URL query
            query.append(name).append("=").append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append("&");
        }

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());

        System.out.println("=== VNPAY HASH DEBUG ===");
        for (String name : fieldNames) {
            String value = vnpParams.get(name);
            System.out.println(name + "=" + value);
        }
        System.out.println("Raw hashData: " + hashData);
        System.out.println("SecureHash: " + vnp_SecureHash);

        query.append("vnp_SecureHashType=HmacSHA512&");
        query.append("vnp_SecureHash=").append(URLEncoder.encode(vnp_SecureHash, StandardCharsets.US_ASCII));

        System.out.println("Final redirect: " + VNPayConfig.vnp_PayUrl + "?" + query);

        return "redirect:" + VNPayConfig.vnp_PayUrl + "?" + query;
    }

    @GetMapping("/vnpay-callback")
    public String handleCallback(@RequestParam Map<String, String> params, Model model) {
        String transactionRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String invoiceIdStr = params.get("vnp_OrderInfo").replace("INV:", "");
        Integer invoiceId = Integer.parseInt(invoiceIdStr);

        Invoice invoice = invoiceService.getById(invoiceId);

        PaymentTransaction txn = new PaymentTransaction();
        txn.setInvoice(invoice);
        txn.setAmount(invoice.getFinalAmount());
        txn.setStatus(responseCode.equals("00") ? "success" : "failed");
        txn.setPaymentMethod("VNPAY");
        txn.setTransactionReference(transactionRef);
        txn.setPaymentDate(LocalDateTime.now());
        txn.setPaymentChannel("ONLINE");
        paymentTransactionRepository.save(txn);

        if (responseCode.equals("00")) {
            invoice.setPaymentStatus("paid");
            invoice.setPaymentDate(LocalDateTime.now());
            invoice.setPaymentMethod("VNPAY");
            invoiceService.save(invoice);
        }

        model.addAttribute("invoice", invoice);
        return "invoice/invoice-result";
    }
}
