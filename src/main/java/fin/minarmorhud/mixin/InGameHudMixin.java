package fin.minarmorhud.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private final int[] steps = new int[4];

    @Unique
    private final int[] colors = new int[4];

    @Unique
    private boolean isActive = false;

    @Inject(at = @At("TAIL"), method = "extractItemHotbar")
    private void renderDurability(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        if (!isActive) return;

        final int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        final int x = screenWidth / 2 - 7;
        int y = screenHeight - 34 - (this.minecraft.player.experienceLevel > 0 ? 5 : 0);

        for (int i = 0; i < 4; ++i, y -= 3) {
            int step = steps[i];
            if (step == -1) continue;
            int color = colors[i];

            context.fill(
                    x, y,
                    x + 13, y + 2,
                    0xFF000000
            );

            context.fill(
                    x, y,
                    x + step, y + 1,
                    color
            );
        }
    }

    @Inject(at = @At("TAIL"), method = "tick()V")
    private void tick(CallbackInfo ci) {
        if (this.minecraft.player == null) return;
        isActive = tickArmor();
    }

    @Unique
    private boolean tickArmor() {
        if (this.minecraft.player.isCreative()) return false;

        final boolean feet = tickArmorPiece(EquipmentSlot.FEET, 0);
        final boolean legs = tickArmorPiece(EquipmentSlot.LEGS, 1);
        final boolean chest = tickArmorPiece(EquipmentSlot.CHEST, 2);
        final boolean head = tickArmorPiece(EquipmentSlot.HEAD, 3);

        return feet || legs || chest || head;
    }

    @Unique
    private boolean tickArmorPiece(EquipmentSlot equipmentSlot, int i) {
        ItemStack armor = this.minecraft.player.getItemBySlot(equipmentSlot);

        int step = -1, color = -1;
        if (!armor.isEmpty()) {
            step = armor.getBarWidth();
            color = armor.getBarColor() | 0xFF000000;
        }

        steps[i] = step;
        colors[i] = color;

        return step != -1;
    }
}