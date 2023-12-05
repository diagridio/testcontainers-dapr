package io.diagrid.dapr;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.Assert;

public class DaprPlacementContainerTest {


    @ClassRule()
    public static DaprPlacementContainer placement = new DaprPlacementContainer("daprio/placement");



    @Test
    public void testDaprPlacementContainerDefaults(){
        Assert.assertEquals("The default port is set", 50006,
                    placement.getPort());             
        
    }

   

}
