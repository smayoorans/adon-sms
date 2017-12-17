package lk.dialog.ideabiz.subscription;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lk.dialog.ideabiz.api.sampleapp.MainController;
import lk.dialog.ideabiz.library.APICall.APICall;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import lk.ideabiz.api.model.common.APIResponse.Response;
import lk.ideabiz.api.model.common.PIN.PINSubmit;
import lk.ideabiz.api.model.common.PIN.SubscriptionResponse;
import lk.ideabiz.api.model.common.Payment.*;
import lk.ideabiz.api.model.common.Subscription.v3.SubscriptionRequest;
import lk.ideabiz.api.model.common.Subscription.v3.SubscriptionResponseWrap;
import lk.ideabiz.api.model.common.Subscription.v3.SubscriptionStatusResponse;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SubscriptionHandler {

    private static final Logger logger = Logger.getLogger(MainController.class);
    private String url;
    private APICall apiCall;
    private Gson gson;

    public SubscriptionHandler(String url, APICall apiCall) {
        this.url = url;
        this.apiCall = apiCall;
        this.gson = (new GsonBuilder()).serializeNulls().create();
    }

    public SubscriptionResponse sendSubscriptionRequest(String msisdn) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        try {
            SubscriptionRequest subscriptionRequst = new SubscriptionRequest();
            subscriptionRequst.setMethod("ANC");
            subscriptionRequst.setMsisdn(msisdn);
            logger.info("Sending subscription request for msisdn: " + msisdn);
            APICallResponse response = this.apiCall.sendAuthAPICall(1, this.url, "POST", header, this.gson.toJson(subscriptionRequst), false);
            if (response.getStatusCode() == 200) {
                Response response1 = this.gson.fromJson(response.getBody(), Response.class);
                String str = response1.getData().toString();
                String replace = str.replace("tel:+", "");
                SubscriptionResponse subscriptionResponse = this.gson.fromJson(replace, SubscriptionResponse.class);
                return subscriptionResponse;
            }
            return null;
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    public APICallResponse sendUnSubscriptionRequest(String msisdn) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        try {
            SubscriptionRequest subscriptionRequst = new SubscriptionRequest();
            subscriptionRequst.setMethod("ANC");
            subscriptionRequst.setMsisdn(msisdn);
            return this.apiCall.sendAuthAPICall(1, this.url, "POST", header, this.gson.toJson(subscriptionRequst), false);
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    public SubscriptionStatusResponse sendSubscriptionStatusCheckRequest() throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        try {
            APICallResponse response = this.apiCall.sendAuthAPICall(1, this.url, "GET", header, null, false);
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody();
                Response fromJson = this.gson.fromJson(responseBody, Response.class);
                SubscriptionResponseWrap data = this.gson.fromJson(fromJson.getData().toString(), SubscriptionResponseWrap.class);
                logger.info("Subscription status response received. " + data.getSubscribeResponse().getStatus() + ":" + data.getSubscribeResponse().getMsisdn());
                return data.getSubscribeResponse();
            }
            return null;
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }


    public APICallResponse verifyPin(String serverRef, String pin) throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        try {
            PINSubmit pinSubmit = new PINSubmit();
            pinSubmit.setPin(pin);
            pinSubmit.setServerRef(serverRef);
            return this.apiCall.sendAuthAPICall(1, this.url, "POST", header, this.gson.toJson(pinSubmit), false);
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }

    public APICallResponse charge() throws Exception {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");
        try {
            PaymentRequestWrapper paymentRequestWrapper = new PaymentRequestWrapper();

            final AmountTransactionRequest amountTransaction = new AmountTransactionRequest();
            amountTransaction.setClientCorrelator("54321");
            amountTransaction.setEndUserId("tel:+94774421435");

            final PaymentAmount paymentAmount = new PaymentAmount();

            final ChargingInformation chargingInformation = new ChargingInformation();
            chargingInformation.setAmount(1.0);
            chargingInformation.setCurrency("LKR");
            chargingInformation.setDescription("Test charge");

            final ChargingMetaData chargingMetaData = new ChargingMetaData();
            chargingMetaData.setChannel("WAP");
            chargingMetaData.setOnBehalfOf("IdeaBiz Test");
            chargingMetaData.setTaxAmount(0.0);
            chargingMetaData.setServiceID(null);

            paymentAmount.setChargingMetaData(chargingMetaData);
            paymentAmount.setChargingInformation(chargingInformation);

            amountTransaction.setPaymentAmount(paymentAmount);
            amountTransaction.setReferenceCode("REF-12345");
            amountTransaction.setTransactionOperationStatus("Charged");

            paymentRequestWrapper.setAmountTransaction(amountTransaction);

            return this.apiCall.sendAuthAPICall(1, this.url, "POST", header, this.gson.toJson(paymentRequestWrapper), false);
        } catch (Exception ex) {
            logger.error(ex);
            throw ex;
        }
    }
}
