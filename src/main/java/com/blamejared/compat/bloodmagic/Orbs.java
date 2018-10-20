package com.blamejared.compat.bloodmagic;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.*;
import crafttweaker.api.item.IIngredient;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.*;
import WayofTime.bloodmagic.core.RegistrarBloodMagic;
import WayofTime.bloodmagic.core.registry.OrbRegistry;
import WayofTime.bloodmagic.orb.BloodOrb;

import java.util.List;

@ZenClass("mods.bloodmagic.Orbs")
@ZenRegister
@ModOnly("bloodmagic")
public class Orbs {
    private static ResourceLocation transformName (String name) {
        return new ResourceLocation((name.contains(":")) ? name : "bloodmagic:"+name);
    }

    @ZenMethod
    public static IIngredient getOrb (String name) {
        if (!RegistrarBloodMagic.BLOOD_ORBS.containsKey(transformName(name))) {
            CraftTweakerAPI.logError("Blood Magic orb doesn't exist: "+name);
            return null;
        }
        BloodOrb orb = RegistrarBloodMagic.BLOOD_ORBS.getValue(transformName(name));
        List<ItemStack> orbList = OrbRegistry.getOrbsDownToTier(orb.getTier());

        if (orbList.size() == 0) {
            CraftTweakerAPI.logError("Blood Magic orb tier returned invalid orbs: "+name);
            return null;
        }

        IIngredient suitableOrbs = new MCItemStack(orbList.get(0));

        for (ItemStack item : orbList) {
            suitableOrbs = suitableOrbs.or(new MCItemStack(item));

        }

        return suitableOrbs;
    }
}