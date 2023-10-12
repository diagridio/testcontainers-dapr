package io.diagrid.dapr;

import java.util.Base64;
import io.dapr.client.domain.State;

import io.restassured.RestAssured;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;

import org.junit.ClassRule;
import org.junit.Test;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

public class DaprContainerTests {

    @ClassRule
    public static  DaprContainer daprContainer = new DaprContainer("daprio/daprd");
    
    private String STATE_STORE_NAME = "statestore";
    private String KEY = "key";

    @Test
    public void testBasicUsage() throws Exception {

        try (DaprClient client = (new DaprClientBuilder()).build()) {
        
            // Save state
            client.saveState(STATE_STORE_NAME, KEY, "value").block();

            // assertThat(response.body().jsonPath().getString("[0].Value"))
            //     .isEqualTo(Base64.getEncoder().encodeToString("value123".getBytes(StandardCharsets.UTF_8)));
            
            State<String> retrievedState = client.getState(STATE_STORE_NAME, KEY, String.class).block();



            
        }catch(Exception ex){
            ex.printStackTrace();
            
        }
            
  
    }

    
}
