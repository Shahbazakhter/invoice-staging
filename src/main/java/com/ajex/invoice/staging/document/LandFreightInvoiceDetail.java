package com.ajex.invoice.staging.document;

import com.ajex.invoice.staging.dto.AimsInvoiceData;
import com.ajex.invoice.staging.dto.CustomerInvoiceInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "invoice-details")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LandFreightInvoiceDetail extends InvoiceDetail {
	
	private AimsInvoiceData aimsInvoiceData;
	
	private CustomerInvoiceInfo customerInfo;

}
