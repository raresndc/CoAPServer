import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.eclipse.californium.scandium.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.DtlsClientConnector;

import java.net.InetSocketAddress;
import java.util.Base64;

public class SecureCoAPClient {
    public static void main(String[] args) {
        try {
            // PSK (Pre-Shared Key) setup
            String pskIdentity = "client1";
            String pskKey = "secretPSK";

            StaticPskStore pskStore = new StaticPskStore(pskIdentity, pskKey.getBytes());

            DtlsConnectorConfig.Builder config = new DtlsConnectorConfig.Builder()
                    .setPskStore(pskStore)
                    .setAddress(new InetSocketAddress(0)); // Random port

            DtlsClientConnector dtlsConnector = new DtlsClientConnector(config.build());

            CoapClient client = new CoapClient("coaps://localhost:5684/secure");
            client.setEndpoint(dtlsConnector);

            // Send secure POST request
            String plaintextPayload = "Hello, Secure CoAP Server!";
            System.out.println("Sending Payload: " + plaintextPayload);

            Request request = new Request(CoAP.Code.POST);
            request.setPayload(plaintextPayload);

            Response response = client.advanced(request);
            System.out.println("Received Encrypted Response: " + response.getResponseText());

            // Decode response (mock decryption with Base64)
            String decryptedResponse = new String(Base64.getDecoder().decode(response.getResponseText()));
            System.out.println("Decrypted Response: " + decryptedResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
