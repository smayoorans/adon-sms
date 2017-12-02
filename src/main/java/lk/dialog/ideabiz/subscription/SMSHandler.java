package lk.dialog.ideabiz.subscription;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lk.dialog.ideabiz.library.APICall.APICall;
import lk.ideabiz.api.model.common.sms.Outbound.OutboundSMSMessageRequest;
import lk.ideabiz.api.model.common.sms.Outbound.OutboundSMSMessagingRequestWrap;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMSHandler {

    public String URL;
    public APICall apiCalll;
    public Integer OAuthId;
    public String senderName;
    public String senderAddress;
    public Gson gson;
    public Logger logger;

    public SMSHandler(String URL, APICall apiCalll, Integer OAuthId, String senderName, String senderAddress) {
        this.URL = URL;
        this.apiCalll = apiCalll;
        this.OAuthId = OAuthId;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.gson = (new GsonBuilder()).serializeNulls().create();
    }

    public boolean sendSMS(ArrayList<String> addresses, String message) throws Exception {
        return this.sendSMS(this.senderAddress, this.senderName, addresses, message);
    }

    public boolean sendSMS(String port, String senderName, ArrayList<String> addresses, String message) throws Exception {
        OutboundSMSMessagingRequestWrap outboundSMSMessagingRequestWrap = new OutboundSMSMessagingRequestWrap();
        OutboundSMSMessageRequest outboundSMSMessageRequest = new OutboundSMSMessageRequest(addresses, message, port, senderName);
        outboundSMSMessagingRequestWrap.setOutboundSMSMessageRequest(outboundSMSMessageRequest);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");

        try {
            this.apiCalll.sendAuthAPICall(this.OAuthId, this.URL, "POST", header, this.gson.toJson(outboundSMSMessagingRequestWrap), false);
            return true;
        } catch (Exception var10) {
            this.logger.error(var10);
            throw var10;
        }
    }
}
