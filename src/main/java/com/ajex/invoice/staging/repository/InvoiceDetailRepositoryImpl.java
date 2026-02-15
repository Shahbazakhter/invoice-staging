package com.ajex.invoice.staging.repository;

import com.ajex.invoice.staging.constant.InvoiceDetailStatus;
import com.ajex.invoice.staging.document.LandFreightInvoiceDetail;
import com.ajex.invoice.staging.dto.InvoiceFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ajex.invoice.staging.constant.InvoiceDetailStatus.INVOICE_STAGE_UPDATING;

@Repository
@RequiredArgsConstructor
public class InvoiceDetailRepositoryImpl implements InvoiceDetailRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<LandFreightInvoiceDetail> filter(InvoiceFilterRequest req,
                                                 int page,
                                                 int size) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(req.getBusinessUnits())) {
            criteriaList.add(
                    Criteria.where("businessUnit")
                            .in(req.getBusinessUnits())
            );
        }

        if (!CollectionUtils.isEmpty(req.getCustomerSubAccounts())) {
            criteriaList.add(
                    Criteria.where("aimsInvoiceData.subAccountNo")
                            .in(req.getCustomerSubAccounts())
            );
        }

        if (!CollectionUtils.isEmpty(req.getWaybillNos())) {
            criteriaList.add(
                    Criteria.where("waybillNo")
                            .in(req.getWaybillNos())
            );
        }
        if (!CollectionUtils.isEmpty(req.getStatuses())) {
            criteriaList.add(
                    Criteria.where("status")
                            .in(req.getStatuses())
            );
        }
        criteriaList.add(Criteria.where("status")
                        .nin(INVOICE_STAGE_UPDATING));
/*

        // date range filter
        if (req.getFromDate() != null || req.getToDate() != null) {
            Criteria dateCriteria = Criteria.where("createdDate");

            if (req.getFromDate() != null)
                dateCriteria.gte(req.getFromDate());

            if (req.getToDate() != null)
                dateCriteria.lte(req.getToDate());

            criteriaList.add(dateCriteria);
        }
*/

        // combine all criteria
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList));
        }

//        // pagination + sorting
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
//        query.with(pageable);

        return mongoTemplate.find(query, LandFreightInvoiceDetail.class);
    }
}
