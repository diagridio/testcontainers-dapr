package io.diagrid.dapr;

import java.util.Collections;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import io.diagrid.dapr.DaprContainer.Component;
import io.diagrid.dapr.DaprContainer.MetadataEntry;

public class DaprComponentTest {
    
    @Test
    public void componentSerializationTest(){
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Representer representer = new YamlRepresenter(options);
        representer.addClassTag(MetadataEntry.class, Tag.MAP);
        Yaml yaml = new Yaml(representer);

        DaprContainer dapr = new DaprContainer("daprio/daprd")
                                                        .withAppName("dapr-app")
                                                        .withAppPort(8081)
                                                        .withComponent(new Component("statestore", "state.in-memory", Collections.singletonMap("actorStateStore", new QuotedBoolean("true"))))
                                                        .withAppChannelAddress("host.testcontainers.internal");

        Set<Component> components = dapr.getComponents();                                                        
        Assert.assertEquals(1, components.size());
        Component kvstore = components.iterator().next();
        Assert.assertEquals(false, kvstore.getMetadata().isEmpty());

        String componentYaml = yaml.dumpAsMap(dapr.componentToMap(kvstore));

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
}
