import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;
import org.eclipse.californium.scandium.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.DtlsServerConnector;

import java.net.InetSocketAddress;
import java.util.Base64;

public class SecureCoAPServer {

    public static void main(String[] args) {
        try {
            // PSK (Pre-Shared Key) setup
            String pskIdentity = "client1";
            String pskKey = "secretPSK";

            StaticPskStore pskStore = new StaticPskStore(pskIdentity, pskKey.getBytes());

            DtlsConnectorConfig.Builder config = new DtlsConnectorConfig.Builder()
                    .setAddress(new InetSocketAddress(5684)) // DTLS default port
                    .setPskStore(pskStore);

            DtlsServerConnector dtlsConnector = new DtlsServerConnector(config.build());

            CoapServer server = new CoapServer();
            server.addEndpoint(dtlsConnector);
            server.add(new SecureResource());
            server.start();

            System.out.println("Secure CoAP server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class SecureResource extends CoapResource {
        public SecureResource() {
            super("secure");
            getAttributes().setTitle("Secure Resource");
        }

        @Override
        public void handlePOST(CoapExchange exchange) {
            String payload = exchange.getRequestText();
            System.out.println("Received Payload: " + payload);

            // Encrypt payload as response (for simplicity, use Base64 as a mock example)
            String encryptedResponse = Base64.getEncoder().encodeToString(("Response to: " + payload).getBytes());
            exchange.respond(encryptedResponse);
        }
    }
}
