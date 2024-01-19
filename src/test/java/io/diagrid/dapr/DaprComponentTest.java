package io.diagrid.dapr;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import io.diagrid.dapr.DaprContainer.Component;
import io.diagrid.dapr.DaprContainer.Subscription;

public class DaprComponentTest {
    
    @Test
    public void componentStateStoreSerializationTest(){
    

        DaprContainer dapr = new DaprContainer("daprio/daprd")
                                                        .withAppName("dapr-app")
                                                        .withAppPort(8081)
                                                        .withComponent(new Component("statestore", "state.in-memory", Collections.singletonMap("actorStateStore", new QuotedBoolean("true"))))
                                                        .withAppChannelAddress("host.testcontainers.internal");

        Set<Component> components = dapr.getComponents();                                                        
        Assert.assertEquals(1, components.size());
        Component kvstore = components.iterator().next();
        Assert.assertEquals(false, kvstore.getMetadata().isEmpty());

        String componentYaml = dapr.componentToYAML(kvstore);

        String expectedComponentYAML = "metadata:\n" + 
                "  name: statestore\n" + 
                "apiVersion: dapr.io/v1alpha1\n" + 
                "kind: Component\n" +
                "spec:\n" + 
                "  metadata:\n" + 
                "  - name: actorStateStore\n" +
                "    value: \"true\"\n" +
                "  type: state.in-memory\n" +
                "  version: v1\n";

        Assert.assertEquals(expectedComponentYAML, componentYaml);
        
    }

    @Test
    public void subscriptionSerializationTest(){
         DaprContainer dapr = new DaprContainer("daprio/daprd")
                                                        .withAppName("dapr-app")
                                                        .withAppPort(8081)
                                                        .withSubscription("my-subscription", "pubsub", "topic", "/events")
                                                        .withAppChannelAddress("host.testcontainers.internal");

        Set<Subscription> subscriptions = dapr.getSubscriptions();
        Assert.assertEquals(1, subscriptions.size());

        
        String subscriptionYaml = dapr.subscriptionToYAML(subscriptions.iterator().next());
        System.out.println(subscriptionYaml);

        String expectedSubscriptionYAML = "metadata:\n" +
                                      "  name: my-subscription\n" +
                                      "apiVersion: dapr.io/v1alpha1\n" +
                                      "kind: Subscription\n" +
                                      "spec:\n" +
                                      "  route: /events\n" +
                                      "  pubsubname: pubsub\n" +
                                      "  topic: topic\n";
        Assert.assertEquals(expectedSubscriptionYAML, subscriptionYaml);                                  
    }

    @Test
    public void withComponentFromPath() {

        URL stateStoreYaml = this.getClass().getClassLoader().getResource("components/statestore.yaml");
        Path path = Paths.get(stateStoreYaml.getPath());

        DaprContainer dapr = new DaprContainer("daprio/daprd")
                .withAppName("dapr-app")
                .withAppPort(8081)
                .withComponent(path)
                .withAppChannelAddress("host.testcontainers.internal");

        Set<Component> components = dapr.getComponents();
        Assert.assertEquals(1, components.size());
        Component kvstore = components.iterator().next();
        Assert.assertEquals(false, kvstore.getMetadata().isEmpty());

        String componentYaml = dapr.componentToYAML(kvstore);

        String expectedComponentYAML = "metadata:\n" + //
                "  name: statestore\n" + //
                "apiVersion: dapr.io/v1alpha1\n" + //
                "kind: Component\n" + //
                "spec:\n" + //
                "  metadata:\n" + //
                "  - name: name\n" + //
                "    value: keyPrefix\n" + //
                "  - name: value\n" + //
                "    value: name\n" + //
                "  - name: name\n" + //
                "    value: redisHost\n" + //
                "  - name: value\n" + //
                "    value: redis:6379\n" + //
                "  - name: name\n" + //
                "    value: redisPassword\n" + //
                "  - name: value\n" + //
                "    value: ''\n" + //
                "  type: null\n" + //
                "  version: v1\n" + //
                "";

        Assert.assertEquals(expectedComponentYAML, componentYaml);
    }
}
