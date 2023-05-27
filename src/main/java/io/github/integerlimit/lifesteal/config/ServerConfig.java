package io.github.integerlimit.lifesteal.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {
    private static final General GENERAL;
    private static final ForgeConfigSpec GENERAL_SPEC;
    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> spawnProtectionRadius;
        General(ForgeConfigSpec.Builder builder) {
            spawnProtectionRadius = builder
                    .comment("Spawn Block Protection Radius")
                    .define("spawnBlockProtectionRadius", 16);
        }
    }
    static {
        final Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        GENERAL_SPEC = specPair.getRight();
        GENERAL = specPair.getLeft();

    }
    public static General getGeneralConfig() {
        return GENERAL;
    }
    public static ForgeConfigSpec getGeneralSpec() {
        return GENERAL_SPEC;
    }
}
