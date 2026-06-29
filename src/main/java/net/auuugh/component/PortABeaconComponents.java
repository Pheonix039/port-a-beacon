package net.auuugh.component;

import com.mojang.serialization.Codec;
import net.auuugh.BeaconScanner;
import net.auuugh.PortABeacon;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public class PortABeaconComponents {
    //public static final ComponentType<PyramidSize> PYRAMID_SIZE_COMPONENT = register("pyramid_size", builder -> builder.codec(PyramidSize.CODEC));
    public static final ComponentType<Integer> PYRAMID_LAYERS = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of("port-a-beacon", "pyramid_layers"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        //
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(PortABeacon.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void register() {
        //
        PortABeacon.LOGGER.info("Registering components for " + PortABeacon.MOD_ID);
    }
}
