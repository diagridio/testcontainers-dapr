package io.diagrid.dapr;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;
import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DaprContainer extends GenericContainer<DaprContainer> {

    public static class Component {
        String name;

        String type;

        Map<String, String> metadata;

        public Component(String name, String type, Map<String, String> metadata) {
            this.name = name;
            this.type = type;
            this.metadata = metadata;
        }
    }

    private static final int DAPRD_HTTP_PORT = 3500;
    private static final int DAPRD_GRPC_PORT = 50001;
    private final Set<Component> components = new HashSet<>();
    private String appName;
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("daprio/daprd");

    
    public DaprContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        
        // Use the daprd health endpoint to verify that daprd is running
        setWaitStrategy(Wait.forHttp("/v1.0/healthz").forPort(DAPRD_HTTP_PORT).forStatusCode(204));
        withExposedPorts(DAPRD_HTTP_PORT, DAPRD_GRPC_PORT);
    }

    public DaprContainer(String image) {
        this(DockerImageName.parse(image));
    }

    public DaprContainer withComponent(Component component) {
        components.add(component);
        return this;
    }

    public DaprContainer withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public DaprContainer withComponent(String name, String type, Map<String, String> metadata) {
        components.add(new Component(name, type, metadata));
        return this;
    }
    
    public String getHttpEndpoint() {
        return "http://" + getHost() + ":" + getMappedPort(DAPRD_HTTP_PORT);
    }

    public int getGRPCPort() {
        return getMappedPort(DAPRD_GRPC_PORT);
    }


    @Override
    protected void configure() {
        super.configure();
         withCommand(
                "./daprd",
                "-app-id", appName,
                "--dapr-listen-addresses=0.0.0.0",
                "-components-path", "/components"
        );

        if(components.isEmpty()){
            components.add(new Component("statestore", "state.in-memory", Collections.emptyMap()));
            components.add(new Component("pubsub", "pubsub.in-memory", Collections.emptyMap()));
        }

        for (Component component : components) {
            Yaml yaml = new Yaml();
            
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("apiVersion", "dapr.io/v1alpha1");
            componentProps.put("kind", "Component");

            Map<String, String> componentMetadata = new HashMap<>();
            componentMetadata.put("name", component.name);
            componentProps.put("metadata", componentMetadata);

            Map<String, Object> componentSpec = new HashMap<>();
            componentSpec.put("type", component.type);
            componentSpec.put("version", "v1");

            Map<String, String> componentSpecMetadata = new HashMap<>();
            for(Map.Entry<String, String> entry : component.metadata.entrySet()){
                componentSpecMetadata.put(entry.getKey(), entry.getValue());
            }

            componentSpec.put("metadata", componentSpecMetadata);
            componentProps.put("spec", componentSpec);
            String componentYaml = yaml.dump(componentProps);
            
            withCopyToContainer(
                    Transferable.of(componentYaml), "/components/" + component.name + ".yaml"
            );
        }

    }
}