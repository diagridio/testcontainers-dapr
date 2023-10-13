package io.diagrid.dapr;
import io.dapr.client.domain.State;

import static org.junit.Assert.assertThat;

import static org.junit.Assert.assertThat;


import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.junit.Assert;

public class DaprContainerTest {

    @ClassRule
    public static  DaprContainer daprContainer = new DaprContainer("daprio/daprd").withAppName("dapr-app");
    
    private String STATE_STORE_NAME = "statestore";
    private String KEY = "key";

    @BeforeClass
    public static void setDaprProperties() {
        System.setProperty("dapr.grpc.port", Integer.toString(daprContainer.getGRPCPort()));
    }

    @Test
    public void testBasicUsage() throws Exception {

        try (DaprClient client = (new DaprClientBuilder()).build()) {
        
            String value = "value";
            // Save state
            client.saveState(STATE_STORE_NAME, KEY, value).block();

            
            // Get the state back from the state store
            State<String> retrievedState = client.getState(STATE_STORE_NAME, KEY, String.class).block();

            Assert.assertEquals("The value retrieved should be the same as the one stored",value, retrievedState.getValue());


            
        }catch(Exception ex){
            ex.printStackTrace();
            
        }
            
  
    }

    
}
