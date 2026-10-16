package vn.hyturcraft.tutien;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Reader;
import java.io.Writer;

public final class TuTienConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("hyturtutien.json");

    public double xpRequiredMultiplier = 1.0;
    public double mobXpMultiplier = 1.0;
    public boolean meditationEnabled = true;
    public int meditationXpPerSecond = 3;
    public boolean aurasEnabled = true;
    public int auraPulseTicks = 8;
    public int auraParticlesPerPulse = 4;
    public boolean broadcastNewRealm = true;
    public double bonusMaxHpPerLayer = 0.20;
    public double bonusAttackDamagePerLayer = 0.055;
    public double bonusArmorPerLayer = 0.03;
    public double bonusMovementSpeedPerLayer = 0.00012;

    public static TuTienConfig load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader r = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                    TuTienConfig loaded = GSON.fromJson(r, TuTienConfig.class);
                    if (loaded != null) {
                        loaded.validate();
                        return loaded;
                    }
                }
            }
        } catch (Exception e) {
            TuTienMod.LOGGER.warn("Cannot read cultivation config, using defaults", e);
        }
        TuTienConfig defaults = new TuTienConfig();
        defaults.save();
        return defaults;
    }

    private void validate() {
        xpRequiredMultiplier = clamp(xpRequiredMultiplier, 0.05, 100.0);
        mobXpMultiplier = clamp(mobXpMultiplier, 0.0, 100.0);
        meditationXpPerSecond = Math.max(0, Math.min(10000, meditationXpPerSecond));
        auraPulseTicks = Math.max(4, Math.min(200, auraPulseTicks));
        auraParticlesPerPulse = Math.max(1, Math.min(12, auraParticlesPerPulse));
        bonusMaxHpPerLayer = clamp(bonusMaxHpPerLayer, 0.0, 2.0);
        bonusAttackDamagePerLayer = clamp(bonusAttackDamagePerLayer, 0.0, 2.0);
        bonusArmorPerLayer = clamp(bonusArmorPerLayer, 0.0, 2.0);
        bonusMovementSpeedPerLayer = clamp(bonusMovementSpeedPerLayer, 0.0, 0.002);
    }

    private static double clamp(double v, double min, double max) {
        return Double.isFinite(v) ? Math.max(min, Math.min(max, v)) : min;
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer w = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
        } catch (Exception e) {
            TuTienMod.LOGGER.error("Cannot write cultivation config", e);
        }
    }
}
