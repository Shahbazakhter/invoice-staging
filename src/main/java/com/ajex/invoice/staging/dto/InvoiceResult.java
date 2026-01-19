package com.ajex.invoice.staging.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class InvoiceResult {
	
	private final List<InvoiceStatus> successfulInvoices = new ArrayList<>();
	private final List<InvoiceStatus> failedInvoices = new ArrayList<>();
	private String businessLine;
	public InvoiceResult(String businessLine){
		this.businessLine = businessLine;
	}


	@Data
	public static class InvoiceStatus {
		
		private String responseCode;
		private String responseMessage;
		
		private String invoiceId;
		private long invoiceDate;
		private String waybillNumber;
		private String transactionId;
		private long transactionDate;
		private Long invoiceRequestId;

	}
	
}
