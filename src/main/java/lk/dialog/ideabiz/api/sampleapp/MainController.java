package lk.dialog.ideabiz.api.sampleapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lk.dialog.ideabiz.library.LibraryManager;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import lk.dialog.ideabiz.subscription.SMSHandler;
import lk.dialog.ideabiz.subscription.SubscriptionHandler;
import lk.dialog.ideabiz.subscription.SubscriptionRepo;
import lk.ideabiz.api.model.common.APIResponse.Response;
import lk.ideabiz.api.model.common.PIN.SubscriptionResponse;
import lk.ideabiz.api.model.common.Payment.PaymentResponseWrapper;
import lk.ideabiz.api.model.common.Subscription.v3.SubscriptionResponseWrap;
import lk.ideabiz.api.model.common.Subscription.v3.SubscriptionStatusResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/sms/")
public class MainController {

    private final static Logger logger = Logger.getLogger(MainController.class);

    private static final String PIN_SUBSCRIPTION_URL = "https://ideabiz.lk/apicall/pin/subscription/v1/subscribe";
    private static final String VERIFY_PIN_URL = "https://ideabiz.lk/apicall/pin/subscription/v1/submitPin";
    private static final String UN_SUBSCRIPTION_URL = "https://ideabiz.lk/apicall/subscription/v3/unsubscribe";
    private static final String SUBSCRIPTION_CHECK_STATUS_URL = "https://ideabiz.lk/apicall/subscription/v3/status/tel%3A%2B";
    private static final String SMS_SEND_URL = "https://ideabiz.lk/apicall/smsmessaging/v3/outbound/87711/requests";
    private static final String CHARGE_URL = "https://ideabiz.lk/apicall/payment/v3/94773400432/transactions/amount";
    private static Gson gson = null;
    private SubscriptionHandler subscriptionHandler;
    private SMSHandler smsHandler;

    @Autowired
    private SubscriptionRepo subscriptionRepo;

    public MainController() {
        gson = new GsonBuilder().serializeNulls().create();
    }


    @RequestMapping(value = "message", method = RequestMethod.GET)
    public String inbound() {
        return "message";
    }

    @RequestMapping(value = "send-message", method = RequestMethod.POST)
    private ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response) {
        String message = request.getParameter("message");
        processSendMessage(message);
        return new ModelAndView("message", "msg", "Message is sending to all subscribers.");
    }

    private void processSendMessage(String message) {
        if (smsHandler == null)
            smsHandler = new SMSHandler(SMS_SEND_URL, LibraryManager.getApiCall(), 1, "Adon", "tel:87711");

        Boolean status;
        try {
            ArrayList<String> addresses = new ArrayList<>();
            List<SubscriptionResponse> all = subscriptionRepo.getAll();
            for (SubscriptionResponse subscriptionResponse : all) {
                addresses.add("tel:+" + subscriptionResponse.getMsisdn());
            }
            status = smsHandler.sendSMS(addresses, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "subscribe", method = RequestMethod.GET)
    public String subscribePage() {
        charge();
        return "subscribe";
    }

    @RequestMapping(value = "subscribe_action", method = RequestMethod.POST)
    private ModelAndView subscribeAction(HttpServletRequest request, HttpServletResponse response) {
        String msisdn = request.getParameter("msisdn");
        SubscriptionResponse pinResponse = sendPinRequest(msisdn);
        if (pinResponse != null) {
            ModelAndView modelAndView = new ModelAndView("verify_pin");
            modelAndView.addObject("serverRef", pinResponse.getServerRef());
            modelAndView.addObject("msisdn", pinResponse.getMsisdn());
            modelAndView.addObject("msg", "PIN sent to " + pinResponse.getMsisdn());
            return modelAndView;
        } else {
            return new ModelAndView("subscribe");
        }
    }

    @RequestMapping(value = "verify_pin_action", method = RequestMethod.POST)
    private ModelAndView verifyPINAction(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String msisdn = request.getParameter("msisdn");
        String pin = request.getParameter("pin");
        String serverRef = request.getParameter("serverRef");
        String pinResponse = verifyPinRequest(pin, serverRef);
        if ("SUBSCRIBED".equals(pinResponse)) {
            redirectAttributes.addFlashAttribute("msg", "You got subscribed to Adon App");
            return new ModelAndView("redirect:/sms/message");
        } else {

            ModelAndView verify_pin = new ModelAndView("verify_pin");
            verify_pin.addObject("msg", pinResponse);
            verify_pin.addObject("serverRef", serverRef);
            verify_pin.addObject("msisdn", msisdn);
            return verify_pin;
        }
    }

    @RequestMapping(value = "unsubscribe", method = RequestMethod.GET)
    public String subscribe() {
        return "unsubscribe";
    }

    @RequestMapping(value = "unsubscribe_action", method = RequestMethod.POST)
    private ModelAndView unSubscribeAction(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String msisdn = request.getParameter("msisdn");
        String statusResponse = sendUnSubscribe(msisdn);
        if (statusResponse.equals("UNSUBSCRIBED")) {
            ModelAndView message = new ModelAndView("redirect:/sms/message");
            redirectAttributes.addFlashAttribute("msg", "You got unsubscribed from Adon App.");
            return message;
        } else {
            ModelAndView unsubscribe = new ModelAndView("unsubscribe");
            unsubscribe.addObject("msg", "Your phone number wasn't subscribed to Adon App.");
            return unsubscribe;
        }
    }

    private SubscriptionResponse sendPinRequest(String msisdn) {
        subscriptionHandler = new SubscriptionHandler(PIN_SUBSCRIPTION_URL, LibraryManager.getApiCall());
        try {
            SubscriptionResponse response = subscriptionHandler.sendSubscriptionRequest(msisdn);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String verifyPinRequest(String pin, String serverRef) {
        subscriptionHandler = new SubscriptionHandler(VERIFY_PIN_URL, LibraryManager.getApiCall());
        try {
            APICallResponse apiCallResponse = subscriptionHandler.verifyPin(serverRef, pin);
            if (apiCallResponse.getStatusCode() == 200) {
                Response response = gson.fromJson(apiCallResponse.getBody(), Response.class);
                String data = response.getData().toString();
                SubscriptionResponse subscriptionResponse = gson.fromJson(data.replace("tel:+", ""), SubscriptionResponse.class);
                subscriptionRepo.insertSubscriber(subscriptionResponse);

                return subscriptionResponse.getStatus();
            } else {
                Response response = gson.fromJson(apiCallResponse.getBody(), Response.class);
                return response.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String charge() {
        subscriptionHandler = new SubscriptionHandler(CHARGE_URL, LibraryManager.getApiCall());
        try {
            APICallResponse apiCallResponse = subscriptionHandler.charge();
            if (apiCallResponse.getStatusCode() == 200) {
                Response response = gson.fromJson(apiCallResponse.getBody(), Response.class);
                String data = response.getData().toString();
                PaymentResponseWrapper paymentResponseWrapper = gson.fromJson(data, PaymentResponseWrapper.class);
                System.out.println(paymentResponseWrapper);;
                return "Response";
            } else {
                Response response = gson.fromJson(apiCallResponse.getBody(), Response.class);
                return response.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendSubscriptionStatus(String msisdn) {
        subscriptionHandler = new SubscriptionHandler(SUBSCRIPTION_CHECK_STATUS_URL + msisdn, LibraryManager.getApiCall());
        try {
            SubscriptionStatusResponse statusResponse = subscriptionHandler.sendSubscriptionStatusCheckRequest();
            logger.info("Response received." + statusResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendUnSubscribe(String msisdn) {
        subscriptionHandler = new SubscriptionHandler(UN_SUBSCRIPTION_URL, LibraryManager.getApiCall());
        try {
            APICallResponse apiCallResponse = subscriptionHandler.sendUnSubscriptionRequest(msisdn);

//            {"statusCode":"SUCCESS","message":"","data":{"subscribeResponse":{"msisdn":"94773400431","status":"NOT_SUBSCRIBED","serviceID":null}}}

            if (apiCallResponse.getStatusCode() == 200) {
                String responseBody = apiCallResponse.getBody();
                Response fromJson = gson.fromJson(responseBody, Response.class);
                SubscriptionResponseWrap data = gson.fromJson(fromJson.getData().toString(), SubscriptionResponseWrap.class);
                logger.info("Subscription status response received. " + data.getSubscribeResponse().getStatus() + ":" + data.getSubscribeResponse().getMsisdn());
                subscriptionRepo.deleteSubscriber(data.getSubscribeResponse().getMsisdn());
                return data.getSubscribeResponse().getStatus();
            }
            return "Error";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}