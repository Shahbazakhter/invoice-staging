package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.InvoiceFilterRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository {

    List<LandFreightInvoiceDetail> filter(InvoiceFilterRequest request, int page, int size);

}
