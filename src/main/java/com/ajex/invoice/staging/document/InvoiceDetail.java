package com.ajex.invoice.staging.document;

import com.ajex.invoice.staging.constant.BusinessLine;
import com.ajex.invoice.staging.constant.InvoiceDetailStatus;
import com.ajex.invoice.staging.dto.InvoiceError;
import com.ajex.invoice.staging.dto.UserRemarks;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "invoice-details")
@Data
@NoArgsConstructor
public abstract class InvoiceDetail {

    @MongoId
    public ObjectId id;

    @Indexed(unique = true)
    private String waybillNo;
    private String customerAccountNo;
    private String oracleInvoiceId;
    private long oracleInvoiceDate;
    private String transactionId;
    private long transactionDate;

    private String businessLine;
    private String businessUnit;
    private String customerName;

    private String status;

    private List<InvoiceError> errorList;
    private List<UserRemarks> remarksList;

    private String insertionTimestamp;
    private String lastUpdatedTimestamp;

    public InvoiceDetailStatus getStatus() {
        if (status == null) {
            return null;
        }
        return InvoiceDetailStatus.valueOf(status.toUpperCase().replace(" ", "_"));
    }

    public void setStatus(InvoiceDetailStatus status) {
        if (status == null) {
            this.status = null;
        } else {
            this.status = status.getValue().toLowerCase();
        }
    }

    public BusinessLine getBusinessLine() {
        if (businessLine == null) {
            return null;
        }
        return BusinessLine.valueOf(businessLine.toUpperCase());
    }

    public void setBusinessLine(BusinessLine businessLine) {
        if (businessLine == null) {
            this.businessLine = null;
        } else {
            this.businessLine = businessLine.getValue().toLowerCase();
        }
    }

    public List<InvoiceError> getErrorList() {
        if (errorList == null) {
            errorList = new ArrayList<>();
        }

        return this.errorList;
    }

}
