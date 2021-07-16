package io.github.noeppi_noeppi.libx.util;

import io.github.noeppi_noeppi.libx.capability.LongEnergyStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Utility methods to convert amounts (must be > 0) that are stored as {@code long}
 * to {@code int}. This is meant to be used whenever an API only allows int values
 * (like {@link IEnergyStorage}) but you store the value as a long. This will output
 * half of {@link Integer#MAX_VALUE} if there's more energy stored and more free than
 * the maximum int value so things using these APIs will detect that they can extract
 * stuff and also insert it. (Only at half of the possible maximum speed though)
 * 
 * To use this with {@link IEnergyStorage}, you can use {@link LongEnergyStorage}.
 */
public class LongAmountToIntUtil {

    /**
     * Gets a value stored as a long as an int value. For more info see class description.
     */
    public static int getValue(long stored, long max) {
        if (max > Integer.MAX_VALUE && stored > (Integer.MAX_VALUE / 2)) {
            long freeEnergy = max - stored;
            if (freeEnergy < (Integer.MAX_VALUE / 2)) {
                return Math.min(
                        (int) MathHelper.clamp(stored, 0, Integer.MAX_VALUE),
                        Integer.MAX_VALUE - ((int) freeEnergy)
                );
            } else {
                return Integer.MAX_VALUE / 2;
            }
        } else {
            return (int) MathHelper.clamp(stored, 0, Integer.MAX_VALUE);
        }
    }

    /**
     * Gets a maximum value stored as a long as an int value. For more info see class description.
     */
    public static int getMaxValue(long max) {
        return (int) MathHelper.clamp(max, 0, Integer.MAX_VALUE);
    }
}