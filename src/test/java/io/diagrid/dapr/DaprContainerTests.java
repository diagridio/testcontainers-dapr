package io.diagrid.dapr;

import java.util.Base64;
import io.restassured.RestAssured;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;

import org.junit.ClassRule;
import org.junit.Test;

public class DaprContainerTests {

    @ClassRule
    public static  DaprContainer daprContainer = new DaprContainer("daprio/daprd:1.11.3");
    
    @Test
    public void testBasicUsage() throws Exception {

            io.restassured.response.Response response = RestAssured
            .given()
            .when()
            .get("http://" + getHostAndPort() + "/v1/")
            .andReturn();

            assertThat(response.body().jsonPath().getString("[0].Value"))
                .isEqualTo(Base64.getEncoder().encodeToString("value123".getBytes(StandardCharsets.UTF_8)));
  
    }

    private String getHostAndPort() {
        return daprContainer.getHost() + ":" + daprContainer.getMappedPort(DaprContainer.DAPRD_HTTP_PORT);
    }
}
