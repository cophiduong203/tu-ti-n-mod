package vn.hyturcraft.tutien;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.joml.Vector3f;

public final class AuraEffects {
    private AuraEffects() {}

    // All effects are vanilla particles, visible to clients without client-side installation.
    private static final ParticleEffect[] ACCENTS = {
        ParticleTypes.ENCHANT,
        ParticleTypes.HAPPY_VILLAGER,
        ParticleTypes.END_ROD,
        ParticleTypes.PORTAL,
        ParticleTypes.SOUL_FIRE_FLAME,
        ParticleTypes.REVERSE_PORTAL,
        ParticleTypes.FLAME,
        ParticleTypes.TOTEM_OF_UNDYING,
        ParticleTypes.ELECTRIC_SPARK,
        ParticleTypes.FIREWORK
    };

    public static void pulse(ServerPlayerEntity player, int gameTick, TuTienConfig config) {
        if (!config.aurasEnabled || player.isSpectator()) return;
        ServerWorld world = player.getServerWorld();
        CultivationHolder holder = (CultivationHolder) player;
        int stage = holder.hytur$getStage();
        int realm = Realms.realm(stage);
        float[] rgb = Realms.COLORS[realm];
        DustParticleEffect dust = new DustParticleEffect(new Vector3f(rgb[0], rgb[1], rgb[2]), 0.95f + (realm * 0.045f));

        int amount = config.auraParticlesPerPulse;
        double phase = gameTick * 0.14 + player.getId() * 0.41;
        double radius = 0.58 + realm * 0.016;
        for (int i = 0; i < amount; i++) {
            double theta = phase + i * Math.PI * 2.0 / amount;
            double height = 0.30 + ((gameTick * 0.035 + i * 0.37) % 1.5);
            double x = player.getX() + Math.cos(theta) * radius;
            double y = player.getY() + height;
            double z = player.getZ() + Math.sin(theta) * radius;
            world.spawnParticles(dust, x, y, z, 1, 0, 0, 0, 0);
        }
        if (gameTick % (config.auraPulseTicks * 3) < config.auraPulseTicks) {
            double y = player.getY() + 1.0 + (realm * 0.05);
            world.spawnParticles(ACCENTS[realm], player.getX(), y, player.getZ(), 1, 0.38, 0.55, 0.38, 0.01);
        }
    }

    public static void breakthrough(ServerPlayerEntity player, int previousStage, int currentStage) {
        ServerWorld world = player.getServerWorld();
        boolean newRealm = Realms.realm(previousStage) != Realms.realm(currentStage);
        int realm = Realms.realm(currentStage);
        float[] rgb = Realms.COLORS[realm];
        DustParticleEffect dust = new DustParticleEffect(new Vector3f(rgb[0], rgb[1], rgb[2]), 1.7f);

        // Two colored ascending rings around the entire player's body.
        for (int ring = 0; ring < (newRealm ? 4 : 2); ring++) {
            double y = player.getY() + 0.35 + ring * 0.44;
            double radius = 0.70 + ring * 0.10;
            for (int i = 0; i < 20; i++) {
                double angle = (Math.PI * 2 * i / 20.0) + ring * 0.2;
                world.spawnParticles(dust, player.getX() + Math.cos(angle) * radius, y,
                    player.getZ() + Math.sin(angle) * radius, 1, 0, 0.07, 0, 0.01);
            }
        }
        world.spawnParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(),
                newRealm ? 90 : 30, 0.6, 0.9, 0.6, 0.11);
        if (newRealm) {
            world.spawnParticles(ACCENTS[realm], player.getX(), player.getY() + 1.1, player.getZ(),
                50, 1.2, 1.0, 1.2, 0.14);
            world.playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.PLAYERS, 1.3f, 0.9f + realm * 0.04f);
        } else {
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.PLAYERS, 0.8f, 0.95f + Realms.layer(currentStage) * 0.02f);
        }
    }
}
