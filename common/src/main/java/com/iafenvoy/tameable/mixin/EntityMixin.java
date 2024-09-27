package com.iafenvoy.tameable.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    @Final
    private EntityType<?> type;

//    @Inject(method = "getDefaultName", at = @At("HEAD"), cancellable = true)
//    private void changeName(CallbackInfoReturnable<Text> cir) {
//        Entity l = (Entity) (Object) this;
//        if (!(l instanceof MobEntity mob)) return;
//        TameData data = TameData.get(mob);
//        if (data.getOwnerPlayer() != null) {
//            MutableText mutableText = this.type.getName().copy().append(Text.literal("(")).append(data.getOwnerPlayer().getDisplayName()).append(Text.literal(")"));
//            if (data.isSitting()) mutableText.append(Text.literal("S"));
//            mutableText.formatted(Formatting.GRAY);
//            cir.setReturnValue(mutableText);
//        }
//    }
//
//    @Inject(method = "hasCustomName", at = @At("HEAD"), cancellable = true)
//    private void displayName(CallbackInfoReturnable<Boolean> cir) {
//        Entity l = (Entity) (Object) this;
//        if (!(l instanceof MobEntity mob)) return;
//        if (TameData.get(mob).getOwner() != null) cir.setReturnValue(true);
//    }
}
