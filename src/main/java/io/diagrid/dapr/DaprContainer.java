package io.diagrid.dapr;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;
import org.yaml.snakeyaml.Yaml;

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
        withCommand(
                "./daprd",
                "-app-id", appName,
                "--dapr-listen-addresses=0.0.0.0",
                "-components-path", "/components"
        );
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
    
    @Override
    protected void doStart() {
        if(components.isEmpty()){
            components.add(new Component("statestore", "state.in-memory", Map.of()));
            components.add(new Component("pubsub", "pubsub.in-memory", Map.of()));
        }
        super.doStart();
    }

    @Override
    protected void configure() {
        super.configure();
        for (Component component : components) {
            var yaml = new Yaml();

            String componentYaml = yaml.dump(
                    Map.ofEntries(
                            Map.entry("apiVersion", "dapr.io/v1alpha1"),
                            Map.entry("kind", "Component"),
                            Map.entry(
                                    "metadata", Map.ofEntries(
                                            Map.entry("name", component.name)
                                    )
                            ),
                            Map.entry("spec", Map.ofEntries(
                                    Map.entry("type", component.type),
                                    Map.entry("version", "v1"),
                                    Map.entry(
                                            "metadata",
                                            component.metadata.entrySet()
                                                    .stream()
                                                    .map(it -> Map.of("name", it.getKey(), "value", it.getValue()))
                                                    .toList()
                                    )
                            ))
                    )
            );

            withCopyToContainer(
                    Transferable.of(componentYaml), "/components/" + component.name + ".yaml"
            );
        }

    }
}