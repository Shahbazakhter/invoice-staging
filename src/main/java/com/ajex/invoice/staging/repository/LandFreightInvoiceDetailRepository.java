package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LandFreightInvoiceDetailRepository extends MongoRepository<LandFreightInvoiceDetail, ObjectId> {
	
	Optional<LandFreightInvoiceDetail> findByWaybillNo(String waybillNo);

	List<LandFreightInvoiceDetail> findAllByWaybillNoIn(List<String> waybillList);

	List<LandFreightInvoiceDetail> findAllByStatusAndWaybillNoIn(String status, List<String> waybillList);

	List<LandFreightInvoiceDetail> findByStatusAndBusinessLine(String status, String businessLine);

	List<LandFreightInvoiceDetail> findByStatusAndBusinessLineAndTransactionIdNull(String status, String businessLine);

}
