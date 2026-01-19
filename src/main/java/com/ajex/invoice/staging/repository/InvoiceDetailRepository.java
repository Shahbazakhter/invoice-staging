package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.document.InvoiceDetail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailRepository extends MongoRepository<InvoiceDetail, ObjectId> {

}
