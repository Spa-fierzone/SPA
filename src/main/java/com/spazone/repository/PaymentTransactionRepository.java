package com.spazone.repository;

import com.spazone.entity.Invoice;
import com.spazone.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
    List<PaymentTransaction> findByInvoice(Invoice invoice);

    Optional<PaymentTransaction> findByTransactionReference(String orderCode);
}
