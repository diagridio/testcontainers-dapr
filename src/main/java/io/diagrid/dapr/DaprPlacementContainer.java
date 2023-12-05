package io.diagrid.dapr;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class DaprPlacementContainer extends GenericContainer<DaprPlacementContainer>{
    
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("daprio/placement");
    private int PLACEMENT_PORT = 50006;
    
    public DaprPlacementContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
    

        withExposedPorts(PLACEMENT_PORT);
    }

    public DaprPlacementContainer(String image) {
        this(DockerImageName.parse(image));
    }

    @Override
    protected void configure() {
        super.configure();
        withCommand("./placement", "-port",Integer.toString(PLACEMENT_PORT));
    }

    public static DockerImageName getDefaultImageName() {
        return DEFAULT_IMAGE_NAME;
    }

    public DaprPlacementContainer withPort(Integer port) {
        this.PLACEMENT_PORT = port;
        return this;
    }

    public int getPort() {
        return PLACEMENT_PORT;
    }

    
}
