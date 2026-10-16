package vn.hyturcraft.tutien;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TuTienMod implements ModInitializer {
    public static final String MOD_ID = "hyturtutien";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static TuTienConfig CONFIG;
    private static long gameTicks = 0;

    @Override public void onInitialize() {
        CONFIG = TuTienConfig.load();
        registerKills();
        registerTicks();
        registerCommands();
        LOGGER.info("Hytur Tu Tien loaded: 10 realms, 100 cultivation layers, Fabric 1.20.1");
    }

    private static void registerKills() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, killer, dead) -> {
            if (!(killer instanceof ServerPlayerEntity player)) return;
            if (dead instanceof ServerPlayerEntity) return; // no PvP XP farming
            if (dead.getType().getSpawnGroup() != SpawnGroup.MONSTER) return;

            // All hostile mobs, including bosses using vanilla MONSTER spawn group.
            // Rewards scale by target's max HP; capped to avoid trivial instant-max exploits.
            double health = Math.max(1, Math.min(2000, dead.getMaxHealth()));
            long reward = Math.max(1, Math.round((9.0 + health * 0.65) * CONFIG.mobXpMultiplier));
            CultivationManager.addXp(player, reward);
        });
    }

    private static void registerTicks() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            gameTicks++;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (CONFIG.aurasEnabled && gameTicks % CONFIG.auraPulseTicks == 0) {
                    AuraEffects.pulse(player, (int) (gameTicks % 1_000_000L), CONFIG);
                }
                if (gameTicks % 20 == 0 && CONFIG.meditationEnabled
                    && !player.isSpectator() && player.isOnGround() && player.isSneaking()
                    && player.getVelocity().horizontalLengthSquared() < 0.0009) {
                    CultivationManager.addXp(player, CONFIG.meditationXpPerSecond);
                }
                if (gameTicks % 80 == 0 && !player.isSpectator()) {
                    CultivationManager.refreshAttributes(player);
                }
                if (gameTicks % 100 == 0 && !player.isSpectator()) {
                    CultivationManager.displayProgress(player);
                }
            }
        });
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("canhgioi")
                .executes(ctx -> showInfo(ctx.getSource().getPlayerOrThrow()))
                .then(CommandManager.literal("xem")
                    .executes(ctx -> showInfo(ctx.getSource().getPlayerOrThrow())))
                .then(CommandManager.literal("huongdan")
                    .executes(ctx -> {
                        var player = ctx.getSource().getPlayerOrThrow();
                        player.sendMessage(Text.literal("[Tu Tiên] Giết quái thù địch để nhận tu vi. Hoặc đứng yên và giữ Shift để thiền (3 tu vi/giây mặc định)."), false);
                        player.sendMessage(Text.literal("Mỗi 10 tầng thăng một cảnh giới; tổng 10 cảnh giới, 100 tầng. Tự động đột phá khi đủ tu vi."), false);
                        player.sendMessage(Text.literal("/canhgioi xem - xem tiến độ. Cảnh giới được lưu khi chết, logout và đổi chiều không gian."), false);
                        return 1;
                    }))
                .then(CommandManager.literal("addxp")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 1_000_000))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                            CultivationManager.addXp(player, amount);
                            ctx.getSource().sendFeedback(() -> Text.literal("Đã thêm " + amount + " tu vi."), false);
                            return amount;
                        })))
                .then(CommandManager.literal("settang")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("tang", IntegerArgumentType.integer(1, 100))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            int stage = IntegerArgumentType.getInteger(ctx, "tang");
                            CultivationManager.setStage(player, stage);
                            ctx.getSource().sendFeedback(() -> Text.literal("Cảnh giới mới: "
                                + CultivationManager.displayStage(player)), false);
                            return stage;
                        })))
                .then(CommandManager.literal("reload")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> {
                        CONFIG = TuTienConfig.load();
                        for (ServerPlayerEntity p : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                            CultivationManager.refreshAttributes(p);
                        }
                        ctx.getSource().sendFeedback(() -> Text.literal("Đã đọc lại config/hyturtutien.json"), false);
                        return 1;
                    }))
            );
        });
    }

    private static int showInfo(ServerPlayerEntity player) {
        CultivationHolder data = (CultivationHolder) player;
        player.sendMessage(Text.literal("────────── ☯ TU TIÊN ☯ ──────────").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Cảnh giới: " + CultivationManager.displayStage(player))
            .formatted(Realms.color(data.hytur$getStage())), false);
        String xp = Realms.maxed(data.hytur$getStage()) ? "ĐẠI VIÊN MÃN"
            : data.hytur$getCultivationXp() + " / " + CultivationManager.needed(data.hytur$getStage());
        player.sendMessage(Text.literal("Tu vi: " + xp).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("/canhgioi huongdan - cách tu luyện"), false);
        return 1;
    }
}
