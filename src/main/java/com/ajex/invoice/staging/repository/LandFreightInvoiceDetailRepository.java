package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LandFreightInvoiceDetailRepository extends MongoRepository<LandFreightInvoiceDetail, ObjectId> {

    List<LandFreightInvoiceDetail> findAllByWaybillNoIn(List<String> waybillList);

    List<LandFreightInvoiceDetail> findAllByStatusAndWaybillNoIn(String status, List<String> waybillList);

    List<LandFreightInvoiceDetail> findAllByStatusInAndWaybillNoIn(List<String> statuses, List<String> waybillList);

    List<LandFreightInvoiceDetail> findAllByStatus(String status);

    List<LandFreightInvoiceDetail> findByStatusInAndBusinessLine(List<String> statuses, String businessLine);

}
