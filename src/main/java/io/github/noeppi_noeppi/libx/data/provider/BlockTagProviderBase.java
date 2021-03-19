package io.github.noeppi_noeppi.libx.data.provider;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

/**
 * A base class for block tag provider
 */
public class BlockTagProviderBase extends BlockTagsProvider {

    protected final ModX mod;

    public BlockTagProviderBase(ModX mod, DataGenerator generatorIn, ExistingFileHelper fileHelper) {
        super(generatorIn, mod.modid, fileHelper);
        this.mod = mod;
    }

    @Nonnull
    @Override
    public final String getName() {
        return this.mod.modid + " block tags";
    }
}