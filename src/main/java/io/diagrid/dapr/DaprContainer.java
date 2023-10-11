package io.diagrid.dapr;

import org.testcontainers.containers.GenericContainer;
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

    private final Set<Component> components = new HashSet<>();
    private String appName;

    
    public DaprContainer(DockerImageName image) {
        super(image);
        
        withCommand(
                "./daprd",
                "-app-id", appName,
                "--dapr-listen-addresses=0.0.0.0",
                "-components-path", "/components"
        );
        withExposedPorts(50001);
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