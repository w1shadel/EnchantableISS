package com.maxwell.enchantable_iss.events;

import com.maxwell.enchantable_iss.EnchantableISS;
import com.maxwell.enchantable_iss.init.ArcaneEnchantmentInit;
import com.maxwell.enchantable_iss.util.SchoolPowerEnchantment;
import com.maxwell.enchantable_iss.util.SchoolResistanceEnchantment;
import io.redspace.ironsspellbooks.api.events.ModifySpellLevelEvent;
import io.redspace.ironsspellbooks.api.events.SpellCooldownAddedEvent;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellOnCastEvent;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = EnchantableISS.MODID)
public class ArcaneEnchantmentEventHandler {
    @SubscribeEvent
    public static void onModifySpellLevel(ModifySpellLevelEvent event) {
        if (event.getEntity() == null) return;
        LivingEntity caster = event.getEntity();
        List<ItemStack> itemsToScan = new ArrayList<>();
        caster.getAllSlots().forEach(itemsToScan::add);
        if (caster instanceof Player player) {
            ItemStack spellbook = Utils.getPlayerSpellbookStack(player);
            if (spellbook != null && !spellbook.isEmpty() && !itemsToScan.contains(spellbook)) {
                itemsToScan.add(spellbook);
            }
            applySlotSpecificEnchants(event, player, spellbook);
        }
        final ResourceLocation spellSchoolId = event.getSpell().getSchoolType().getId();
        int bonusLevels = 0;
        for (ItemStack stack : itemsToScan) {
            if (stack == null || stack.isEmpty()) continue;
            var enchants = EnchantmentHelper.getEnchantments(stack);
            for (var entry : enchants.entrySet()) {
                Enchantment enchant = entry.getKey();
                int level = entry.getValue();
                if (enchant == ArcaneEnchantmentInit.SPELL_POWER.get()) {
                    bonusLevels += level;
                } else if (enchant instanceof SchoolPowerEnchantment schoolEnchant) {
                    if (schoolEnchant.getSchoolResource().equals(spellSchoolId)) {
                        bonusLevels += level;
                    }
                }
            }
        }
        event.addLevels(bonusLevels);
    }

    private static void applySlotSpecificEnchants(ModifySpellLevelEvent event, Player player, ItemStack spellbook) {
        if (spellbook == null || spellbook.isEmpty()) return;
        CompoundTag tag = spellbook.getTag();
        if (tag != null && tag.contains("iss_enchanted_slots")) {
            SpellSelectionManager selectionManager = new SpellSelectionManager(player);
            int selectedIndex = selectionManager.getSelectionIndex();
            CompoundTag slotsTag = tag.getCompound("iss_enchanted_slots");
            String slotKey = "slot_" + selectedIndex;
            if (slotsTag.contains(slotKey)) {
                ListTag enchantList = slotsTag.getList(slotKey, 10);
                for (int i = 0; i < enchantList.size(); i++) {
                    CompoundTag enchantTag = enchantList.getCompound(i);
                    ResourceLocation id = ResourceLocation.tryParse(enchantTag.getString("id"));
                    int lvl = enchantTag.getInt("lvl");
                    if (id != null) {
                        processSlotEnchantment(event, id, lvl);
                    }
                }
            }
        }
    }

    private static void processSlotEnchantment(ModifySpellLevelEvent event, ResourceLocation enchantId, int level) {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantId);
        if (enchantment == null) return;
        if (enchantment == ArcaneEnchantmentInit.SPELL_POWER.get()) {
            event.addLevels(level);
        } else if (enchantment instanceof SchoolPowerEnchantment schoolEnchant) {
            if (schoolEnchant.getSchoolResource().equals(event.getSpell().getSchoolType().getId())) {
                event.addLevels(level);
            }
        }
    }

    @SubscribeEvent
    public static void onSpellCast(SpellOnCastEvent event) {
        Player player = event.getEntity();
        if (player == null) return;
        int manaThrift = EnchantmentHelper.getEnchantmentLevel(ArcaneEnchantmentInit.MANA_THRIFT.get(), player);
        ItemStack spellbook = Utils.getPlayerSpellbookStack(player);
        if (spellbook != null && !spellbook.isEmpty()) {
            if (spellbook != player.getMainHandItem() && spellbook != player.getOffhandItem()) {
                manaThrift += EnchantmentHelper.getItemEnchantmentLevel(ArcaneEnchantmentInit.MANA_THRIFT.get(), spellbook);
            }
        }
        if (manaThrift > 0) {
            float reduction = Math.min(manaThrift * 0.08f, 0.80f);
            event.setManaCost((int) (event.getManaCost() * (1.0f - reduction)));
        }
    }

    @SubscribeEvent
    public static void onCooldown(SpellCooldownAddedEvent.Pre event) {
        Player player = event.getEntity();
        if (player == null) return;
        int cdLevel = EnchantmentHelper.getEnchantmentLevel(ArcaneEnchantmentInit.COOLDOWN_REDUCTION.get(), player);
        ItemStack spellbook = Utils.getPlayerSpellbookStack(player);
        if (spellbook != null && !spellbook.isEmpty() && spellbook != player.getMainHandItem() && spellbook != player.getOffhandItem()) {
            cdLevel += EnchantmentHelper.getItemEnchantmentLevel(ArcaneEnchantmentInit.COOLDOWN_REDUCTION.get(), spellbook);
        }
        if (cdLevel > 0) {
            float reduction = 1.0f - (cdLevel * 0.10f);
            event.setEffectiveCooldown((int) (event.getEffectiveCooldown() * reduction));
        }
    }

    @SubscribeEvent
    public static void onSpellDamage(SpellDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim == null) return;
        SpellDamageSource spellSource = event.getSpellDamageSource();
        if (spellSource != null) {
            float reduction = 0.0f;
            int magicProtLevel = EnchantmentHelper.getEnchantmentLevel(ArcaneEnchantmentInit.MAGIC_PROTECTION.get(), victim);
            reduction += (magicProtLevel * 0.05f);
            ResourceLocation damageSchoolId = spellSource.spell().getSchoolType().getId();
            List<ItemStack> defenseItems = new ArrayList<>();
            victim.getAllSlots().forEach(defenseItems::add);
            if (victim instanceof Player player) {
                ItemStack spellbook = Utils.getPlayerSpellbookStack(player);
                if (spellbook != null && !spellbook.isEmpty() && !defenseItems.contains(spellbook)) {
                    defenseItems.add(spellbook);
                }
            }
            for (ItemStack stack : defenseItems) {
                if (stack == null || stack.isEmpty()) continue;
                var enchants = EnchantmentHelper.getEnchantments(stack);
                for (var entry : enchants.entrySet()) {
                    if (entry.getKey() instanceof SchoolResistanceEnchantment resEnchant) {
                        if (resEnchant.getSchoolResource().equals(damageSchoolId)) {
                            reduction += (entry.getValue() * 0.08f);
                        }
                    }
                }
            }
            float finalReduction = Math.min(reduction, 1.0f);
            event.setAmount(event.getAmount() * (1.0f - finalReduction));
        }
    }
}