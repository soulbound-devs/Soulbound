package net.vakror.asm.world.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.vakror.asm.ASMMod;

public class ModDungeonPieces {
    private static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE = DeferredRegister.create(Registries.STRUCTURE_PIECE, ASMMod.MOD_ID);
    public static final RegistryObject<StructurePieceType> DEFAULT_DUNGEON_PIECE = register("default", DungeonPiece::new);

    public ModDungeonPieces() {
    }

    public static RegistryObject<StructurePieceType> register(String name, StructurePieceType type) {
        return STRUCTURE_PIECE.register(name, () -> {
            return type;
        });
    }

    public static void register(IEventBus eventBus) {
        STRUCTURE_PIECE.register(eventBus);
    }
}