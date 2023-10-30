package io.diagrid.dapr;

import io.dapr.client.domain.State;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule; 
import io.dapr.client.domain.Metadata;
import static java.util.Collections.singletonMap;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class DaprContainerTest {

    @ClassRule()
    public static WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(9090)); 

    @ClassRule()
    public static DaprContainer daprContainer = new DaprContainer("daprio/daprd").withAppName("dapr-app").withAppPort(9090);

    private String STATE_STORE_NAME = "statestore";
    private String KEY = "my-key";

    private String PUBSUB_NAME = "pubsub";
    private String PUBSUB_TOPIC_NAME = "topic";
    // Time-to-live for messages published.
    private static final String MESSAGE_TTL_IN_SECONDS = "1000";

    @BeforeClass
    public static void setDaprProperties() {
        configStub();
        System.setProperty("dapr.grpc.port", Integer.toString(daprContainer.getGRPCPort()));
    }


    @Test
    public void testStateStoreAPIs() throws Exception {

        try (DaprClient client = (new DaprClientBuilder()).build()) {
            
            client.waitForSidecar(5000);

            String value = "value";
            // Save state
            client.saveState(STATE_STORE_NAME, KEY, value).block();

            // Get the state back from the state store
            State<String> retrievedState = client.getState(STATE_STORE_NAME, KEY, String.class).block();

            Assert.assertEquals("The value retrieved should be the same as the one stored", value,
                    retrievedState.getValue());

        }

    }

    @Test
    public void testPubSubAPIs() throws Exception {
        

        try (DaprClient client = (new DaprClientBuilder()).build()) {
            
            client.waitForSidecar(5000);
            
            String message = "message content";
            // Save state
            client.publishEvent(
                    PUBSUB_NAME,
                    PUBSUB_TOPIC_NAME,
                    message,
                    singletonMap(Metadata.TTL_IN_SECONDS, MESSAGE_TTL_IN_SECONDS)).block();

        }

        // verify(anyRequestedFor(urlMatching("([a-z1-9]*)")));

        // verify(postRequestedFor(urlEqualTo("/events"))
        //     .withHeader("Content-Type", equalTo("application/json")));

        

    }

    private static void configStub() { 
        
  
        stubFor(any(urlMatching("/dapr/([a-z1-9]*)"))
            .willReturn(aResponse()
            .withBody("[]")
            .withStatus(200)));  

        // create a stub 
        stubFor(post(urlEqualTo("/events"))
            .willReturn(aResponse()
            .withBody("event received!")
            .withStatus(200))); 

        configureFor("localhost", 9090); 
          
    } 

}
