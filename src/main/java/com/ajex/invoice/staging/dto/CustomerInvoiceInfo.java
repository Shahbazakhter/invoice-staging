package com.ajex.invoice.staging.dto;

import lombok.Data;

@Data
public class CustomerInvoiceInfo {
	
	// ..Main Account Info
	private String mainAccount;
	private String companyRegistrationNumber;
	private String companyName;
	private String companyVatNumber;
	
	// ...Sub Account Info
	private String subAccountNo;
	private String subAccountName;
	
	// ...Billing Address
	private String contactName;
	private String contactPhone;
	private String eInvoiceEmail;
	private String country;
	private String city;
	private String postalCode;
	private String address;
	private String phone;
	private String email;
	private String fax;
	
}
