package com.maxwell.enchantable_iss.mixin;

import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InscriptionTableMenu.class, remap = false)
public abstract class MixinInscriptionTableMenu {
    @Shadow
    @Final
    protected ResultContainer resultContainer;
    @Shadow
    private int selectedSpellIndex;

    @Shadow
    public abstract Slot getSpellBookSlot();

    @Shadow
    public abstract Slot getScrollSlot();

    @Inject(method = "doInscription", at = @At("HEAD"))
    private void onDoInscription(int selectedIndex, CallbackInfo ci) {
        ItemStack book = getSpellBookSlot().getItem();
        ItemStack scroll = getScrollSlot().getItem();
        if (!book.isEmpty() && !scroll.isEmpty()) {
            ListTag enchantList = scroll.getEnchantmentTags().copy();
            CompoundTag tag = book.getOrCreateTag();
            if (!tag.contains("iss_enchanted_slots")) {
                tag.put("iss_enchanted_slots", new CompoundTag());
            }
            CompoundTag slotsTag = tag.getCompound("iss_enchanted_slots");
            String slotKey = "slot_" + selectedIndex;
            if (enchantList.isEmpty()) {
                slotsTag.remove(slotKey);
            } else {
                slotsTag.put(slotKey, enchantList);
            }
        }
    }

    @Inject(method = "setupResultSlot", at = @At("TAIL"))
    private void onSetupResultTail(CallbackInfo ci) {
        ItemStack book = getSpellBookSlot().getItem();
        ItemStack resultStack = this.resultContainer.getItem(0);
        if (!book.isEmpty() && !resultStack.isEmpty() && selectedSpellIndex >= 0) {
            CompoundTag tag = book.getTag();
            if (tag != null && tag.contains("iss_enchanted_slots")) {
                CompoundTag slotsTag = tag.getCompound("iss_enchanted_slots");
                String key = "slot_" + selectedSpellIndex;
                if (slotsTag.contains(key)) {
                    ListTag enchants = slotsTag.getList(key, 10).copy();
                    resultStack.getOrCreateTag().put("Enchantments", enchants);
                }
            }
        }
    }

    @Inject(method = "setupResultSlot", at = @At("TAIL"), remap = false)
    private void onSetupResultTailFixed(CallbackInfo ci) {
        ItemStack book = getSpellBookSlot().getItem();
        ItemStack resultStack = this.resultContainer.getItem(0);
        if (!book.isEmpty() && !resultStack.isEmpty() && selectedSpellIndex >= 0) {
            CompoundTag tag = book.getTag();
            if (tag != null && tag.contains("iss_enchanted_slots")) {
                CompoundTag slotsTag = tag.getCompound("iss_enchanted_slots");
                String key = "slot_" + selectedSpellIndex;
                if (slotsTag.contains(key)) {
                    ListTag enchants = slotsTag.getList(key, 10);
                    resultStack.getOrCreateTag().put("Enchantments", enchants);
                }
            }
        }
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), remap = true)
    private void onQuickMove(Player playerIn, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (index == 38) {
            clearBookNBTOnTake();
        }
    }

    private void clearBookNBTOnTake() {
        ItemStack book = getSpellBookSlot().getItem();
        if (!book.isEmpty() && selectedSpellIndex >= 0) {
            CompoundTag tag = book.getTag();
            if (tag != null && tag.contains("iss_enchanted_slots")) {
                tag.getCompound("iss_enchanted_slots").remove("slot_" + selectedSpellIndex);
            }
        }
    }
}