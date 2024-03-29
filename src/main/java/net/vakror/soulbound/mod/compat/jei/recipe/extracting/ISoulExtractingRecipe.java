package net.vakror.soulbound.mod.compat.jei.recipe.extracting;

import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Unmodifiable;

/**
 * A recipe in JEI that displays soul extraction stuff.
 */
public interface ISoulExtractingRecipe {
    /**
     * The amount of soul to extract from the player
     */
    @Unmodifiable
    int getPlayerSoulAmount();

    /**
     * The amount of dark soul to extract from the player
     */
    @Unmodifiable
    int getPlayerDarkSoulAmount();

    /**
     * The soul fluid stack
     */
    @Unmodifiable
    FluidStack getSoulFluid();

    /**
     * The dark soul fluid stack
     */
    @Unmodifiable
    FluidStack getDarkSoulFluid();
}
