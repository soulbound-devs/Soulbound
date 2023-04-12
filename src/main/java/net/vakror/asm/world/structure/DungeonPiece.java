package net.vakror.asm.world.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;

public class DungeonPiece extends TemplateStructurePiece {
    DungeonTheme theme;
    private final StructureTemplateManager manager;

    public DungeonPiece(StructurePieceType pType, int pGenDepth, StructureTemplateManager manager, ResourceLocation pLocation, String pTemplateName, StructurePlaceSettings pPlaceSettings, BlockPos pos, DungeonTheme theme) {
        super(pType, pGenDepth, manager, pLocation, pTemplateName, pPlaceSettings, pos);
        this.manager = manager;
    }

    private DungeonPiece(StructureTemplateManager manager, DungeonTheme theme, ResourceLocation templateName, BlockPos templatePos, @Nullable ConfiguredFeature<?, ?> tree, Rotation rotation, Mirror mirror) {
        super(ModDungeonPieces.DEFAULT_DUNGEON_PIECE.get(), 0, manager, templateName, templateName.toString(), makeSettings(rotation, mirror), templatePos);
        this.theme = theme;
        this.manager = manager;
    }

    public DungeonPiece(StructurePieceSerializationContext context, CompoundTag nbt) {
        super(ModDungeonPieces.DEFAULT_DUNGEON_PIECE.get(), nbt, context.structureTemplateManager(), (con) -> makeSettings(Rotation.valueOf(nbt.getString("Rotation")), Mirror.valueOf(nbt.getString("Mirror"))));
        this.theme = DungeonTheme.getThemeFromIndex(nbt.getInt("Theme"));
        this.manager = context.structureTemplateManager();
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation, Mirror mirror) {
        return (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK).setRotation(rotation).setMirror(mirror);
    }

    public static void generateDungeon(BlockPos pos, Rotation rotation, StructureTemplateManager manager, StructurePiecesBuilder builder) {
        DungeonPiece piece = new DungeonPiece(ModDungeonPieces.DEFAULT_DUNGEON_PIECE.get(), 0, manager, new ResourceLocation("asm", "dungeon_room_50"), "dungeon_room_50", makeSettings(rotation, Mirror.NONE), pos, DungeonTheme.getRandomTheme());
        builder.addPiece(piece);
    }

    protected void addAdditionalSaveData(StructurePieceSerializationContext pContext, CompoundTag pTag) {
        pTag.putString("theme", this.theme.id());
        super.addAdditionalSaveData(pContext, pTag);
    }

    protected void handleDataMarker(String pName, BlockPos pPos, ServerLevelAccessor pLevel, RandomSource pRandom, BoundingBox pBox) {
    }
}