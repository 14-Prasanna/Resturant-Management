package org.restaurant.service.otp;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpService {

    private static final Dotenv dotenv = Dotenv.load();


    private static final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN  = dotenv.get("TWILIO_AUTH_TOKEN");
    // Support either TWILIO_FROM_NUMBER or TWILIO_PHONE_NUMBER
    private static final String FROM_NUMBER = dotenv.get("TWILIO_FROM_NUMBER") != null ? 
                                              dotenv.get("TWILIO_FROM_NUMBER") : 
                                              dotenv.get("TWILIO_PHONE_NUMBER");


    private final Map<String, String> otpMap = new HashMap<>();

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public boolean sendOtp(String toPhoneNumber) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            String otp = generateOtp();
            otpMap.put(toPhoneNumber, otp);

            Message.creator(
                    new PhoneNumber("+91" + toPhoneNumber),
                    new PhoneNumber(FROM_NUMBER),
                    "Your OTP is: " + otp
            ).create();

            System.out.println("OTP sent successfully to " + toPhoneNumber);
            return true;

        } catch (Exception e) {
            System.out.println("Failed to send OTP: " + e.getMessage());
            return false;
        }
    }

    public boolean verifyOtp(String phone, String enteredOtp) {
        String stored = otpMap.get(phone);

        if (stored == null) {
            System.out.println("No OTP generated!");
            return false;
        }

        if (stored.equals(enteredOtp.trim())) {
            otpMap.remove(phone);
            return true;
        }

        return false;
    }
}