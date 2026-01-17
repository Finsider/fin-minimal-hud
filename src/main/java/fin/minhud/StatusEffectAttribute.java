package fin.minhud;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

public record StatusEffectAttribute(int maxDuration, int amplifier, boolean isAmbient) {

    // this Implementation is inspired from @SoRadGaming Simple-HUD-Enhanced StatusEffectTracker class
    // see: https://github.com/SoRadGaming/Simple-HUD-Enhanced/blob/main/src/main/java/com/soradgaming/simplehudenhanced/utli/StatusEffectsTracker.java

    private static final Map<RegistryEntry<StatusEffect>, StatusEffectAttribute> STATUS_EFFECT_ATTRIBUTE_MAP = new HashMap<>();

    // maxDuration for maxDuration, amplifier and isAmbient to help updating the map.

    public static StatusEffectAttribute create(StatusEffectInstance instance) {
        return new StatusEffectAttribute(
            instance.getDuration(),
            instance.getAmplifier(),
            instance.isAmbient()
        );
    }

    public static StatusEffectAttribute get(StatusEffectInstance instance) {
        return STATUS_EFFECT_ATTRIBUTE_MAP.computeIfAbsent(instance.getEffectType(), key -> create(instance));
    }

    public static void update(StatusEffectInstance instance) {
        STATUS_EFFECT_ATTRIBUTE_MAP.put(instance.getEffectType(), create(instance));
    }

    public static void remove(RegistryEntry<StatusEffect> effectRegistry) {
        STATUS_EFFECT_ATTRIBUTE_MAP.remove(effectRegistry);
    }

    public static boolean shouldUpdate(StatusEffectInstance current, StatusEffectAttribute cached) {
        return  current.getAmplifier() != cached.amplifier() || // different Amplifier: update
                current.isAmbient() != cached.isAmbient() || // different Ambient: update
                current.getDuration() > cached.maxDuration(); // higher Duration: update
    }

    public static Map<RegistryEntry<StatusEffect>, StatusEffectAttribute> getMap() {
        return STATUS_EFFECT_ATTRIBUTE_MAP;
    }
}