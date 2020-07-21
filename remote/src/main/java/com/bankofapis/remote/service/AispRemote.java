package com.bankofapis.remote.service;

import com.bankofapis.core.model.accounts.*;
import com.bankofapis.core.model.common.HttpRequestHeader;
import com.bankofapis.remote.util.BaseApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.bankofapis.remote.common.Endpoints.*;

public class AispRemote {

    private static final Logger logger = LoggerFactory.getLogger(AispRemote.class);

    private final RestTemplate securedRestTemplate;
    private final BaseApiUtils apiUtils;


    public AispRemote(RestTemplate securedRestTemplate, BaseApiUtils apiUtils) {
        this.securedRestTemplate = securedRestTemplate;
        this.apiUtils = apiUtils;
    }

    public OBReadDomesticConsentResponse createAispConsent(OBReadDomesticConsent obReadDataDomesticConsent, HttpRequestHeader httpRequestHeader) {

        HttpEntity<OBReadDomesticConsent> accountConsentRequest =
                apiUtils.createRequest(obReadDataDomesticConsent, httpRequestHeader);

       return securedRestTemplate.postForEntity(apiUtils.getUri(ACCOUNT_ACCESS_CONSENT_ENDPOINT),
                        accountConsentRequest, OBReadDomesticConsentResponse.class).getBody();

    }

    public String createAuthorizeUri(String consentId) {
        return apiUtils.createAuthorizeUrl(consentId);
    }

    public OBReadDataResponse<OBReadAccountList> getAccountResponse(HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_LIST_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadAccountList>>() {
                }).getBody();

    }

    public OBReadDataResponse<OBReadAccountList> getAccountById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadAccountList>>() {
                }, accountId).getBody();

    }

    public OBReadDataResponse<OBReadBalanceList> getBalanceById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_BALANCES_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadBalanceList>>() {
                }, accountId).getBody();
    }

    public OBReadDataResponse<OBReadTransactionList> getTransactionsById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_TRANSACTIONS_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadTransactionList>>() {
                }, accountId).getBody();
    }

    public OBReadDataResponse<OBReadDirectDebitList> getDirectDebitsById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_DIRECT_DEBITS_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadDirectDebitList>>() {
                }, accountId).getBody();

    }

    public OBReadDataResponse<OBReadStandingOrderList> getStandingOrdersById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_STANDING_ORDERS_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadStandingOrderList>>() {
                }, accountId).getBody();

    }

    public OBReadDataResponse<OBReadProductList> getProductById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_PRODUCT_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadProductList>>() {
                }, accountId).getBody();

    }

    public OBReadDataResponse<OBReadBeneficiaryList> getBeneficiariesById(String accountId, HttpRequestHeader httpRequestHeader) {

        return securedRestTemplate.exchange(
                apiUtils.getUri(ACCOUNT_ID_BENEFICIARIES_ENDPOINT),
                HttpMethod.GET,
                apiUtils.createRequest(null, httpRequestHeader),
                new ParameterizedTypeReference<OBReadDataResponse<OBReadBeneficiaryList>>() {
                }, accountId).getBody();

    }

    public List<CredibilityAccount> getCredibility(HttpRequestHeader httpRequestHeader) {

        OBReadDataResponse<OBReadAccountList> accountListResponse = getAccountResponse(httpRequestHeader);
        OBReadAccountList accountListData = accountListResponse.getData();
        List<OBReadAccountInformation> accountList= accountListData.getAccount();
        List<CredibilityAccount> credibilityAccountList = new ArrayList<>();
        for (OBReadAccountInformation account:accountList
             ) {
            CredibilityAccount credibilityAccount = new CredibilityAccount();
            credibilityAccount.setAccountId(account.getAccountId());
            credibilityAccount.setAccountType(account.getAccountType());
            credibilityAccount.setAccountSubType(account.getAccountSubType());

            OBReadDataResponse<OBReadBalanceList> balanceResponse = getBalanceById(account.getAccountId(),httpRequestHeader);
            OBReadBalanceList balanceListData = balanceResponse.getData();
            List<OBReadBalance> balanceList = balanceListData.getAccount();

            credibilityAccount.setBalance(Double.parseDouble(balanceList.get(0).getAmount().getAmount()));
            credibilityAccount.setCurrency(balanceList.get(0).getAmount().getCurrency());



            OBReadDataResponse<OBReadTransactionList> transactionListResponse= getTransactionsById(account.getAccountId(),httpRequestHeader);
            OBReadTransactionList transactionListData = transactionListResponse.getData();
            List<OBReadTransaction> transactionList = transactionListData.getTransactionList();
            LocalDate oneYearAgoDate = LocalDate.now().plusYears(-3);
            double yearlyDebit = 0.0, yearlyCredit = 0.0;
            for (OBReadTransaction transaction: transactionList)
            {
                String modifiedDate = transaction.getBookingDateTime().substring(0,10);
                System.out.println(modifiedDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(modifiedDate,formatter);
                if(!date.isBefore(oneYearAgoDate))
                {
                    if(transaction.getCreditDebitIndicator().equalsIgnoreCase("Credit"))
                    {
                        yearlyCredit = yearlyCredit + Double.parseDouble(transaction.getAmount().getAmount());
                    }
                    else if(transaction.getCreditDebitIndicator().equalsIgnoreCase("Debit"))
                    {
                        yearlyDebit = yearlyDebit + Double.parseDouble(transaction.getAmount().getAmount());
                    }
                }
            }
            credibilityAccount.setYearlyDebit(yearlyDebit);
            credibilityAccount.setYearlyCredit(yearlyCredit);
            credibilityAccountList.add(credibilityAccount);
        }

        return credibilityAccountList;

    }
}