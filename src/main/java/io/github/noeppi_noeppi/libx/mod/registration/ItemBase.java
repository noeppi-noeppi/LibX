package io.github.noeppi_noeppi.libx.mod.registration;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * Base class for items for mods using {@link ModXRegistration}. This will automatically set the
 * creative tab if it's defined in the mod.
 */
public class ItemBase extends Item {

    protected final ModX mod;

    public ItemBase(ModX mod, Properties properties) {
        super(((Supplier<Properties>) () -> {
            if (mod.tab != null) {
                properties.group(mod.tab);
            }
            return properties;
        }).get());
        this.mod = mod;
    }
}
