package net.vakror.soulbound.mod.seal.seals.amplifying.wand.haste;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.vakror.soulbound.mod.seal.ISeal;
import net.vakror.soulbound.mod.seal.SealRegistry;
import net.vakror.soulbound.mod.seal.function.amplify.HasteAmplifyFunction;
import net.vakror.soulbound.mod.seal.tier.seal.TieredWithAmount;
import net.vakror.soulbound.mod.seal.type.amplifying.ItemAmplifyingSeal;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HasteSeal extends ItemAmplifyingSeal implements TieredWithAmount {
    private final int tier;
    public HasteSeal(int tier) {
        super("mining_speed_tier_" + tier);
        this.tier = tier;
        addAmplifyFunction(new HasteAmplifyFunction(getAmount(tier), AttributeModifier.Operation.ADDITION));
    }

    @Override
    public float getAmount(int tier) {
        return (float) tier / 2;
    }

    @Nullable
    @Override
    public ISeal getNextSeal() {
        return SealRegistry.allSeals.getOrDefault("mining_speed_tier_" + (tier + 1), null);
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getTierId() {
        return "soulbound:haste";
    }

    @Override
    public List<ISeal> getAllSeals() {
        return List.of(new HasteSeal(1), new HasteSeal(2), new HasteSeal(3));
    }
}
