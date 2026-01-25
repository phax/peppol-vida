<?xml version="1.0" encoding="UTF-8"?><schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pxc="urn:peppol:xslt:custom-function" queryBinding="xslt2">
	<title>OpenPeppol ViDA TDD Schematron</title>
	<p id="about">These are the Schematron rules for the OpenPeppol ViDA TDD.</p>
	<xsl:function name="pxc:genPath" as="xs:string">
		<xsl:param name="node" as="node()"/>
		<xsl:sequence select="         string-join(for $ancestor in $node/ancestor-or-self::node()                     return                       if ($ancestor instance of element())                       then concat('/',                                   name($ancestor),                                   if (   count($ancestor/preceding-sibling::*[name() = name($ancestor)]) &gt;    0                                       or count($ancestor/following-sibling::*[name() = name($ancestor)]) &gt; 0)                                   then concat('[', count($ancestor/preceding-sibling::*[name() = name($ancestor)]) + 1, ']')                                   else ''                                   )                       else                         if ($ancestor instance of attribute())                         then concat('/@', name($ancestor))                         else ''                     , '')     "/>
	</xsl:function>
	<ns prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2"/>
	<ns prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2"/>
	<ns prefix="cec" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2"/>
	<ns prefix="pxs" uri="urn:peppol:schema:taxdata:1.0"/>
	<ns prefix="inv" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2"/>
	<ns prefix="cn" uri="urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2"/>
	<ns prefix="pxc" uri="urn:peppol:xslt:custom-function"/>
	<pattern id="default">
		<let name="cl_dtc" value="' S R D '"/>
		<let name="cl_ds" value="' D IC Intl '"/>
		<let name="cl_rr" value="' C2 C3 '"/>
		<let name="cl_currency" value="' AED AFN ALL AMD ANG AOA ARS AUD AWG AZN BAM BBD BDT BGN BHD BIF BMD BND BOB BOV BRL BSD BTN BWP BYN BZD CAD CDF CHE CHF CHW CLF CLP CNY COP COU CRC CUP CVE CZK DJF DKK DOP DZD EGP ERN ETB EUR FJD FKP GBP GEL GHS GIP GMD GNF GTQ GYD HKD HNL HTG HUF IDR ILS INR IQD IRR ISK JMD JOD JPY KES KGS KHR KMF KPW KRW KWD KYD KZT LAK LBP LKR LRD LSL LYD MAD MDL MGA MKD MMK MNT MOP MRU MUR MVR MWK MXN MXV MYR MZN NAD NGN NIO NOK NPR NZD OMR PAB PEN PGK PHP PKR PLN PYG QAR RON RSD RUB RWF SAR SBD SCR SDG SEK SGD SHP SLE SOS SRD SSP STD SVC SYP SZL THB TJS TMT TND TOP TRY TTD TWD TZS UAH UGX USD USN UYI UYU UYW UZS VED VES VND VUV WST XAF XAG XAU XBA XBB XBC XBD XCD XDR XOF XPD XPF XPT XSU XTS XUA YER ZAR ZMW ZWG XXX CNH '"/>
		<let name="regex_pidscheme" value="'^[0-9]{4}$'"/>
		<rule context="/pxs:TaxData">
			<let name="dtc" value="normalize-space(pxs:DocumentTypeCode)"/>
			<let name="dcc" value="normalize-space(pxs:DocumentCurrencyCode)"/>
			<let name="ds" value="normalize-space(pxs:DocumentScope)"/>
			<let name="rr" value="normalize-space(pxs:ReporterRole)"/>
			<let name="rtCount" value="count(pxs:ReportedTransaction)"/>
			<assert id="ibr-tdd-00" flag="fatal" test="count(*[not(          self::cbc:CustomizationID          or self::cbc:ProfileID          or self::pxs:UUID          or self::cbc:IssueDate or self::cbc:IssueTime          or self::pxs:DocumentTypeCode or self::pxs:DocumentCurrencyCode          or self::pxs:DocumentScope          or self::pxs:TaxAuthority          or self::pxs:ReporterRole          or self::pxs:ReportingParty          or self::pxs:ReceivingParty          or self::pxs:ReportersRepresentative          or self::pxs:ReportedTransaction          or self::pxs:ReportedDocument)]) = 0">[ibr-tdd-00] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-01" flag="fatal" test="normalize-space(cbc:CustomizationID) = 'urn:peppol:schema:taxdata:1.0::TaxData##urn:peppol:taxdata:ViDA-1::1.0'">[ibr-tdd-01] - The Specification identifier (tdt-001) ID MUST use the value 'urn:peppol:schema:taxdata:1.0::TaxData##urn:peppol:taxdata:ViDA-1::1.0'</assert>
			<assert id="ibr-tdd-02" flag="fatal" test="normalize-space(cbc:ProfileID) = 'urn:peppol:taxreporting'">[ibr-tdd-02] - The Business process type (tdt-002) MUST use the value 'urn:peppol:taxreporting'</assert>
			<assert id="ibr-tdd-03" flag="fatal" test="exists(pxs:UUID)">[ibr-tdd-03] - The Tax Data Document UUID (tdt-003) MUST be present</assert>
			<assert id="ibr-tdd-04" flag="fatal" test="string-length(normalize-space(cbc:IssueDate)) = 10">[ibr-tdd-04] - The Tax Data Document issue date (tdt-004) MUST NOT contain timezone information</assert>
			<assert id="ibr-tdd-05" flag="fatal" test="matches(normalize-space(cbc:IssueTime), '([+-]\d{2}:\d{2}|Z)$')">[ibr-tdd-05] - The Tax Data Document issue time (tdt-005) MUST contain timezone information</assert>
			<assert id="ibr-tdd-06" flag="fatal" test="not(contains($dtc, ' ')) and contains($cl_dtc, concat(' ', $dtc, ' '))">
				[ibr-tdd-06] - The Tax Data Document type code (tdt-007) MUST be coded according to the code list
			</assert>
			<assert id="ibr-tdd-07" flag="fatal" test="not(contains($dcc, ' ')) and contains($cl_currency, concat(' ', $dcc, ' '))">
				[ibr-tdd-07] - The Tax Data Document currency code (tdt-008) MUST be coded according to the code list
			</assert>
			<assert id="ibr-tdd-08" flag="fatal" test="not(contains($ds, ' ')) and contains($cl_ds, concat(' ', $ds, ' '))">
				[ibr-tdd-08] - The Report scope (tdt-006) MUST be coded according to the code list
			</assert>
			<assert id="ibr-tdd-09" flag="fatal" test="not(contains($rr, ' ')) and contains($cl_rr, concat(' ', $rr, ' '))">
				[ibr-tdd-09] - The Reporters role (tdt-012) MUST be coded according to the code list
			</assert>
			<assert id="ibr-tdd-10" flag="fatal" test="exists(pxs:TaxAuthority)">[ibr-tdd-10] - A Tax Data Document MUST contain a Tax Authority segment (tdg-04).</assert>
			<assert id="ibr-tdd-11" flag="fatal" test="$rtCount = 1">
				[ibr-tdd-11] - Exactly one REPORTED TRANSACTION (tdg-001) MUST be present.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:TaxAuthority">
			<assert id="ibr-tdd-12" test="count(*[not(self::cbc:ID or self::cbc:Name)]) = 0">[ibr-tdd-12] - TaxAuthority (tdg-04) can only contain cbc:ID and cbc:Name (tdt-010, tdt-011)</assert>
			<assert id="ibr-tdd-13" flag="fatal" test="exists(cbc:ID)">[tdd-013] - A Tax Authority MUST have a Tax Authority Identifier (tdt-010).</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportingParty">
			<assert id="ibr-tdd-14" flag="fatal" test="every $child in ('MarkCareIndicator', 'MarkAttentionIndicator', 'WebsiteURI', 'LogoReferenceID', 'IndustryClassificationCode', 'PartyIdentification', 'PartyName', 'Language', 'PostalAddress', 'PhysicalLocation', 'PartyTaxScheme', 'PartyLegalEntity', 'Contact', 'Person', 'AgentParty', 'ServiceProviderParty', 'PowerOfAttorney', 'FinancialAccount') satisfies count (*[local-name(.) = $child]) = 0">[ibr-tdd-14] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-15" flag="fatal" test="exists(cbc:EndpointID)">[ibr-tdd-15] Reporters - Endpoint (tdt-013) MUST be present</assert>
			<assert id="ibr-tdd-16" flag="fatal" test="exists(cbc:EndpointID/@schemeID)">[ibr-tdd-16] - Reporters Endpoint Scheme identifier (tdt-013-1) MUST be present</assert>
			<assert id="ibr-tdd-17" flag="fatal" test="not(exists(cbc:EndpointID/@schemeID)) or matches(cbc:EndpointID/@schemeID, $regex_pidscheme)">[ibr-tdd-17] - Reporters Endpoint Scheme identifier (tdt-007-1) MUST be a Peppol Participant Identifier Scheme</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReceivingParty">
			<assert id="ibr-tdd-18" flag="fatal" test="every $child in ('MarkCareIndicator', 'MarkAttentionIndicator', 'WebsiteURI', 'LogoReferenceID', 'IndustryClassificationCode',                                                                   'PartyIdentification', 'PartyName', 'Language', 'PostalAddress', 'PhysicalLocation', 'PartyTaxScheme',                                                                   'PartyLegalEntity', 'Contact', 'Person', 'AgentParty', 'ServiceProviderParty', 'PowerOfAttorney', 'FinancialAccount')                                                     satisfies count (*[local-name(.) = $child]) = 0">[ibr-tdd-18] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-19" flag="fatal" test="exists(cbc:EndpointID)">[ibr-tdd-19] - Receivers Endpoint (tdt-014) MUST be present</assert>
			<assert id="ibr-tdd-20" flag="fatal" test="exists(cbc:EndpointID/@schemeID)">[ibr-tdd-20] - Receivers Endpoint Scheme identifier (tdt-014-1) MUST be present</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportersRepresentative">
			<let name="pidCount" value="count(cac:PartyIdentification/cbc:ID)"/>
			<assert id="ibr-tdd-21" flag="fatal" test="every $child in ('MarkCareIndicator', 'MarkAttentionIndicator', 'WebsiteURI', 'EndpointID', 'LogoReferenceID',                                                                    'IndustryClassificationCode', 'PartyName', 'Language', 'PostalAddress', 'PhysicalLocation', 'PartyTaxScheme',                                                                   'PartyLegalEntity', 'Contact', 'Person', 'AgentParty', 'ServiceProviderParty', 'PowerOfAttorney', 'FinancialAccount')                                                     satisfies count (*[local-name(.) = $child]) = 0">[ibr-tdd-21] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-22" flag="fatal" test="$pidCount = 1">
				[ibr-tdd-22] - Exactly one Reporters Representative ID (tdt-015) MUST be present but found
				<value-of select="$pidCount"/>
				instead
			</assert>
			<assert id="ibr-tdd-23" flag="fatal" test="exists(cac:PartyIdentification/cbc:ID/@schemeID)">[ibr-tdd-23] - Reporters Representative ID Scheme identifier (tdt-015-1) MUST be present</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction">
			<assert id="ibr-tdd-24" flag="fatal" test="exists(pxs:ReportedDocument)">[ibr-tdd-24] - The REPORTED DOCUMENT (tdg-02) MUST be present</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument">
			<assert id="ibr-tdd-25" flag="fatal" test="count(*[not(          self::cbc:CustomizationID          or self::cbc:ProfileID          or self::cbc:ID          or self::cbc:UUID          or self::cbc:IssueDate          or self::pxs:DocumentTypeCode          or self::cbc:Note    or self::cbc:TaxPointDate          or self::cbc:DocumentCurrencyCode          or self::cbc:TaxCurrencyCode          or self::cac:InvoicePeriod          or self::cac:BillingReference          or self::cac:AccountingSupplierParty          or self::cac:AccountingCustomerParty          or self::cac:TaxRepresentativeParty or self::cac:Delivery          or self::cac:PaymentMeans          or self::cac:AllowanceCharge          or self::cac:TaxTotal          or self::pxs:MonetaryTotal          or self::pxs:DocumentLine       )]) = 0">[ibr-tdd-25] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:InvoicePeriod">
			<assert id="ibr-tdd-26" flag="fatal" test="count(*[not(self::cbc:StartDate or self::cbc:EndDate or self::cbc:DescriptionCode)]) = 0">[ibr-tdd-26] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:InvoicePeriod">
			<assert id="ibr-tdd-27" flag="fatal" test="count(*[not(self::cbc:StartDate or self::cbc:EndDate)]) = 0">[ibr-tdd-27] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:BillingReference">
			<assert id="ibr-tdd-84" flag="fatal" test="count(*[not(self::cac:InvoiceDocumentReference)]) = 0">[ibr-tdd-84] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:BillingReference/cac:InvoiceDocumentReference">
			<assert id="ibr-tdd-28" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:IssueDate)]) = 0">[ibr-tdd-28] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingSupplierParty">
			<assert id="ibr-tdd-29" flag="fatal" test="exists(cac:Party)">[ibr-tdd-29] The cac:Party element MUST be present</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingSupplierParty/cac:Party">
			<assert id="ibr-tdd-30" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme)]) = 0">[ibr-tdd-30] cac:Party must not contain elements other than cac:PostalAddress (bg-05) and optional cac:PartyTaxScheme</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress">
			<assert id="ibr-tdd-31" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-31] - cac:PostalAddress (bg-05) must not contain element children other than cac:Country.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-32" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-32] - cac:Country must not contain element children other than cbc:IdentificationCode (bt-040)</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme">
			<assert id="ibr-tdd-33" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-33] - cac:PartyTaxScheme's cac:TaxScheme/cbc:ID must equal 'VAT'.</assert>
			<assert id="ibr-tdd-34" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-34] - cac:PartyTaxScheme must not contain elements other than cbc:CompanyID (bt-031) and cac:TaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-35" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-35] - cac:TaxScheme must not contain element children other than cbc:ID.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedTransaction/pxs:ReportedDocument/cac:AccountingCustomerParty">
			<assert id="ibr-tdd-36" flag="fatal" test="exists(cac:Party)">[ibr-tdd-36] - The cac:Party element MUST be present</assert>
			<assert id="ibr-tdd-37" flag="fatal" test="count(*[not(self::cac:Party)]) = 0">[ibr-tdd-37] - cac:AccountingCustomerParty (bg-07) must not contain element children other than cac:Party.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:Party">
			<assert id="ibr-tdd-38" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme)]) = 0">[ibr-tdd-38] - cac:Party must not contain elements other than cac:PostalAddress (bg-08) and optional cac:PartyTaxScheme</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress">
			<assert id="ibr-tdd-39" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-39] - cac:PostalAddress (bg-08) must not contain element children other than cac:Country.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-40" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-40] - cac:Country must not contain element children other than cbc:IdentificationCode (bt-055)</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme">
			<assert id="ibr-tdd-41" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-41] - cac:PartyTaxScheme's cac:TaxScheme/cbc:ID must equal 'VAT'.</assert>
			<assert id="ibr-tdd-42" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-42] - cac:PartyTaxScheme must not contain elements other than cbc:CompanyID (bt-048) and cac:TaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-43" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-43] - cac:TaxScheme must not contain element children other than cbc:ID.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:AccountingCustomerParty/cac:TaxRepresentativeParty">
			<assert id="ibr-tdd-44" flag="fatal" test="count(*[not(self::cac:PostalAddress or self::cac:PartyTaxScheme)]) = 0">[ibr-tdd-44] - cac:Party must not contain elements other than cac:PostalAddress (bg-12) and optional cac:PartyTaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:TaxRepresentativeParty/cac:PostalAddress">
			<assert id="ibr-tdd-45" flag="fatal" test="count(*[not(self::cac:Country)]) = 0">[ibr-tdd-45] - cac:PostalAddress (bg-12) must not contain element children other than cac:Country.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:TaxRepresentativeParty/cac:PostalAddress/cac:Country">
			<assert id="ibr-tdd-46" flag="fatal" test="count(*[not(self::cbc:IdentificationCode)]) = 0">[ibr-tdd-46] - cac:Country must not contain element children other than cbc:IdentificationCode (bt-069)</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:Delivery">
			<assert id="ibr-tdd-85" flag="fatal" test="count(*[not(self::cbc:ActualDeliveryDate)]) = 0">[ibr-tdd-85] - cac:Delivery (bg-13) must not contain element children other than cbc:ActualDeliveryDate (bt-072)</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:TaxRepresentativeParty/cac:PartyTaxScheme">
			<assert id="ibr-tdd-47" flag="fatal" test="cac:TaxScheme/cbc:ID = 'VAT'">[ibr-tdd-47] - cac:PartyTaxScheme's cac:TaxScheme/cbc:ID must equal 'VAT'.</assert>
			<assert id="ibr-tdd-48" flag="fatal" test="count(*[not(self::cbc:CompanyID or self::cac:TaxScheme)]) = 0">[ibr-tdd-48] - cac:PartyTaxScheme must not contain elements other than cbc:CompanyID (bt-063) and cac:TaxScheme.</assert>
		</rule>
		<rule context="/pxs:TaxData/cac:TaxRepresentativeParty/cac:PartyTaxScheme/cac:TaxScheme">
			<assert id="ibr-tdd-49" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-49] - cac:TaxScheme must not contain element children other than cbc:ID.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans">
			<assert id="ibr-tdd-50" flag="fatal" test="count(*[not(          self::cbc:PaymentMeansCode          or self::cbc:PaymentID          or self::cac:CardAccount          or self::cac:PayeeFinancialAccount          or self::cac:PaymentMandate       )]) = 0">[ibr-tdd-50] Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cbc:PaymentMeansCode">
			<assert id="ibr-tdd-51" flag="fatal" test="count(@*[not(local-name() = 'name')]) = 0">[ibr-tdd-51] - cbc:PaymentMeansCode can only have attribute 'name' (bt-082)</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cac:CardAccount">
			<assert id="ibr-tdd-52" flag="fatal" test="count(*[not(self::cbc:PrimaryAccountNumberID or self::cbc:NetworkID or self::cbc:HolderName)]) = 0">[ibr-tdd-52] Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cac:PayeeFinancialAccount">
			<assert id="ibr-tdd-53" flag="fatal" test="count(*[not(self::cbc:ID or self::cac:FinancialInstitutionBranch)]) = 0">[ibr-tdd-53] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch">
			<assert id="ibr-tdd-54" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-54] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cac:PaymentMandate">
			<assert id="ibr-tdd-55" flag="fatal" test="count(*[not(self::cbc:ID or self::cac:PayerFinancialAccount)]) = 0">[ibr-tdd-55] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:PaymentMeans/cac:PaymentMandate/cac:PayerFinancialAccount">
			<assert id="ibr-tdd-56" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-56] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:AllowanceCharge">
			<assert id="ibr-tdd-57" flag="fatal" test="count(*[not(        self::cbc:ChargeIndicator        or self::cbc:AllowanceChargeReasonCode        or self::cbc:AllowanceChargeReason        or self::cbc:MultiplierFactorNumeric        or self::cbc:Amount        or self::cbc:BaseAmount        or self::cac:TaxCategory     )]) = 0">[ibr-tdd-57] Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-58" flag="fatal" test="count(cbc:Amount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-58] - cbc:Amount (bt-092, bt-099) must have attribute 'currencyID'.</assert>
			<assert id="ibr-tdd-59" flag="fatal" test="count(cbc:BaseAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-59] - cbc:BaseAmount (bt-093, bt-100) must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:AllowanceCharge">
			<assert id="ibr-tdd-60" flag="fatal" test="count(*[not(        self::cbc:ChargeIndicator        or self::cbc:AllowanceChargeReasonCode        or self::cbc:AllowanceChargeReason        or self::cbc:MultiplierFactorNumeric        or self::cbc:Amount        or self::cbc:BaseAmount     )]) = 0">[ibr-tdd-60] Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-61" flag="fatal" test="count(cbc:Amount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-61] - cbc:Amount (bt-136, bt-141) must have attribute 'currencyID'.</assert>
			<assert id="ibr-tdd-62" flag="fatal" test="count(cbc:BaseAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-62] - cbc:Amount (bt-137, bt-142) must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:AllowanceCharge/cac:TaxCategory">
			<assert id="ibr-tdd-63" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cac:TaxScheme)]) = 0">[ibr-tdd-63] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-64" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-64] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:TaxTotal">
			<assert id="ibr-tdd-65" flag="fatal" test="count(*[not(self::cbc:TaxAmount or self::cac:TaxSubtotal)]) = 0">[ibr-tdd-65] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-66" flag="fatal" test="count(cbc:TaxAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-66] - cbc:TaxAmount (bt-110) must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal">
			<assert id="ibr-tdd-67" flag="fatal" test="count(*[not(self::cbc:TaxableAmount or self::cbc:TaxAmount or self::cac:TaxCategory)]) = 0">[ibr-tdd-67] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-68" flag="fatal" test="count(cbc:TaxableAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-68] - cbc:TaxableAmount (bt-116) must have attribute 'currencyID'.</assert>
			<assert id="ibr-tdd-69" flag="fatal" test="count(cbc:TaxAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-69] - cbc:TaxAmount (bt-117) must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory">
			<assert id="ibr-tdd-70" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cbc:TaxExemptionReasonCode or self::cbc:TaxExemptionReason or self::cac:TaxScheme)]) = 0">[ibr-tdd-70] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-71" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-71] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:MonetaryTotal">
			<assert id="ibr-tdd-72" flag="fatal" test="count(*[not(            self::cbc:LineExtensionAmount          or self::cbc:TaxExclusiveAmount          or self::cbc:TaxInclusiveAmount          or self::cbc:AllowanceTotalAmount          or self::cbc:ChargeTotalAmount          or self::cbc:PrepaidAmount          or self::cbc:PayableAmount       )]) = 0">[ibr-tdd-72] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-73" flag="fatal" test="count(*[self::cbc:LineExtensionAmount or self::cbc:TaxExclusiveAmount or self::cbc:TaxInclusiveAmount or self::cbc:AllowanceTotalAmount or self::cbc:ChargeTotalAmount or self::cbc:PrepaidAmount or self::cbc:PayableAmount]/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-73] - Amounts children of pxs:MonetaryTotal must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine">
			<assert id="ibr-tdd-74" flag="fatal" test="count(*[not(           self::cbc:ID         or self::cbc:Note         or self::cbc:InvoicedQuantity         or self::cbc:LineExtensionAmount         or self::cac:InvoicePeriod         or self::cac:AllowanceCharge         or self::cac:Item         or self::cac:Price       )]) = 0">[ibr-tdd-74] - pxs:DocumentLine XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-75" flag="fatal" test="count(cbc:InvoicedQuantity/@*[not(local-name() = 'unitCode')]) = 0">[ibr-tdd-75] - cbc:InvoicedQuantity (bt-129) must have attribute 'unitCode (bt-130)'.</assert>
			<assert id="ibr-tdd-76" flag="fatal" test="count(cbc:LineExtensionAmount/@*[not(local-name() = 'currencyID')]) = 0">[ibr-tdd-76] - cbc:LineExtensionAmount (bt-131) must have attribute 'currencyID'.</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:Item">
			<assert id="ibr-tdd-77" flag="fatal" test="count(*[not(self::cbc:Description or self::cbc:Name or self::cac:CommodityClassification or self::cac:ClassifiedTaxCategory)]) = 0">[ibr-tdd-77] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:CommodityClassification">
			<assert id="ibr-tdd-78" flag="fatal" test="count(*[not(self::cbc:ItemClassificationCode)]) = 0">[ibr-tdd-78] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-79" flag="fatal" test="count(cbc:ItemClassificationCode/@*[not(local-name() = 'listID')]) = 0">[ibr-tdd-79] - cbc:ItemClassificationCode (bt-158) must have attribute 'listID (bt-159-1)'</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:ClassifiedTaxCategory">
			<assert id="ibr-tdd-80" flag="fatal" test="count(*[not(self::cbc:ID or self::cbc:Percent or self::cac:TaxScheme)]) = 0">[ibr-tdd-80] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:Item/cac:ClassifiedTaxCategory/cac:TaxScheme">
			<assert id="ibr-tdd-81" flag="fatal" test="count(*[not(self::cbc:ID)]) = 0">[ibr-tdd-81] - Only XML elements defined in this specification are allowed to be used</assert>
		</rule>
		<rule context="/pxs:TaxData/pxs:ReportedDocument/pxs:DocumentLine/cac:Price">
			<assert id="ibr-tdd-82" flag="fatal" test="count(*[not(self::cbc:PriceAmount)]) = 0">[ibr-tdd-82] - Only XML elements defined in this specification are allowed to be used</assert>
			<assert id="ibr-tdd-83" flag="fatal" test="count(cbc:PriceAmount/@*[not(local-name() = 'currencyID')]) = 0">ibr-tdd-83] - cbc:PriceAmount (bt-146) must have attribute 'currencyID'.</assert>
		</rule>
	</pattern>
</schema>