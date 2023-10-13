package io.diagrid.dapr;
import io.dapr.client.domain.State;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;

public class DaprContainerTests {

    @ClassRule
    public static  DaprContainer daprContainer = new DaprContainer("daprio/daprd").withAppName("dapr-app");
    
    private String STATE_STORE_NAME = "statestore";
    private String KEY = "key";

    @DynamicPropertySource
    static void daprProperties(DynamicPropertyRegistry registry) {
        System.setProperty("dapr.grpc.port", Integer.toString(daprContainer.getGRPCPort()));
    }

    @Test
    public void testBasicUsage() throws Exception {

        try (DaprClient client = (new DaprClientBuilder()).build()) {
        
            // Save state
            client.saveState(STATE_STORE_NAME, KEY, "value").block();

            // assertThat(response.body().jsonPath().getString("[0].Value"))
            //     .isEqualTo(Base64.getEncoder().encodeToString("value123".getBytes(StandardCharsets.UTF_8)));
            
            State<String> retrievedState = client.getState(STATE_STORE_NAME, KEY, String.class).block();

            System.out.println("OK!");

            
        }catch(Exception ex){
            ex.printStackTrace();
            
        }
            
  
    }

    
}
