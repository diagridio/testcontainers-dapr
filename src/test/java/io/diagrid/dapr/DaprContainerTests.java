package io.diagrid.dapr;

import org.junit.Test;

public class DaprContainerTests {
    @Test
    public void testBasicUsage() throws Exception {
        try (
            // daprContainer {
            DaprContainer container = new DaprContainer("daprio/daprd:edge");
            // }
        ) {
            container.start();

        }
    }
}
