package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.document.InvoiceDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceDetailRepository extends MongoRepository<InvoiceDetail, ObjectId> {

    Optional<InvoiceDetail> findByBusinessLineAndWaybillNo(String businessLineA, String waybillNo);

    Collection<InvoiceDetail> findByOracleInvoiceId(String oracleInvoiceId);

    List<InvoiceDetail> findByStatusAndBusinessLine(String status, String businessLine);
}
