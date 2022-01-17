package net.pcal.mobfilter;

import org.junit.jupiter.api.Test;

import static net.minecraft.entity.SpawnGroup.MONSTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MFConfigTest {

    @Test
    public void testHelloWorld() throws Exception {
        final MFConfig.InputStreamSupplier supplier = () ->
                getClass().getClassLoader().getResourceAsStream("test-config.yaml");
//            new FileInputStream("resources/test-config.yaml");
        final MFConfig.ConfigurationFile config = new MFConfig(supplier).reload();

        assertTrue(config.debugEnabled);
        assertEquals(2, config.rules.length);
        assertEquals(MONSTER, config.rules[1].when.spawnGroup[0]);

    }
}
