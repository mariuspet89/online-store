package eu.accesa.onlinestore.configuration;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Bean
    public PayPalHttpClient payPalHttpClient() {
        PayPalEnvironment.Sandbox environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);

        PayPalHttpClient payPalHttpClient = new PayPalHttpClient(environment);
        payPalHttpClient.setConnectTimeout(900);
        return payPalHttpClient;
    }
}
