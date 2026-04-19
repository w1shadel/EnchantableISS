package com.maxwell.enchantable_iss.init;

import com.maxwell.enchantable_iss.EnchantableISS;
import com.maxwell.enchantable_iss.util.MagicEnchantment;
import com.maxwell.enchantable_iss.util.SchoolPowerEnchantment;
import com.maxwell.enchantable_iss.util.SchoolResistanceEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = EnchantableISS.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArcaneEnchantmentInit {
    public static Supplier<Enchantment> MANA_THRIFT;
    public static Supplier<Enchantment> SPELL_POWER;
    public static Supplier<Enchantment> COOLDOWN_REDUCTION;
    public static Supplier<Enchantment> MAGIC_PROTECTION;

    @SubscribeEvent
    public static void registerEnchantments(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ENCHANTMENT)) {
            MANA_THRIFT = register(event, "mana_thrift", () -> new MagicEnchantment(Enchantment.Rarity.RARE, 3, EnchantmentCategory.BREAKABLE, EquipmentSlot.values()));
            SPELL_POWER = register(event, "spell_power", () -> new MagicEnchantment(Enchantment.Rarity.VERY_RARE, 3, EnchantmentCategory.BREAKABLE, EquipmentSlot.values()));
            COOLDOWN_REDUCTION = register(event, "cooldown_reduction", () -> new MagicEnchantment(Enchantment.Rarity.RARE, 3, EnchantmentCategory.BREAKABLE, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND));
            MAGIC_PROTECTION = register(event, "magic_protection", () -> new MagicEnchantment(Enchantment.Rarity.VERY_RARE, 3, EnchantmentCategory.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
            String[] coreSchools = {"fire", "ice", "lightning", "holy", "ender", "blood", "evocation", "nature", "eldritch"};
            for (String path : coreSchools) {
                ResourceLocation schoolId = ResourceLocation.fromNamespaceAndPath("irons_spellbooks", path);
                register(event, path + "_power", () -> new SchoolPowerEnchantment(Enchantment.Rarity.RARE, 10, schoolId));
                register(event, path + "_resist", () -> new SchoolResistanceEnchantment(Enchantment.Rarity.RARE, 10, schoolId));
            }
        }
    }

    private static Supplier<Enchantment> register(RegisterEvent event, String name, Supplier<Enchantment> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EnchantableISS.MODID, name);
        event.register(Registries.ENCHANTMENT, id, supplier);
        return () -> ForgeRegistries.ENCHANTMENTS.getValue(id);
    }
}