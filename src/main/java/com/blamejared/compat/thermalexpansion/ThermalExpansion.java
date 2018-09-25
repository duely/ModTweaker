package com.blamejared.compat.thermalexpansion;

import net.minecraft.item.*;
import crafttweaker.mc1120.data.*;
import net.minecraftforge.fluids.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.util.text.*;
import net.minecraft.server.*;
import net.minecraft.command.*;
import crafttweaker.*;
import crafttweaker.api.liquid.*;
import crafttweaker.api.item.*;
import crafttweaker.api.entity.*;
import crafttweaker.mc1120.commands.*;
import cofh.thermalexpansion.util.managers.machine.*;
import cofh.thermalexpansion.util.managers.device.*;
import java.util.*;

public class ThermalExpansion
{
    public static String parseItemName(ItemStack item) {
        int meta = item.getMetadata();
        String itemName = "<" + item.getItem().getRegistryName() + ((meta == 0) ? "" : (":" + meta)) + ">";
        String withNBT = "";
        if (item.serializeNBT().hasKey("tag")) {
             String nbt = NBTConverter.from(item.serializeNBT().getTag("tag"), false).toString();
            if (nbt.length() > 0) {
                withNBT = ".withTag(" + nbt + ")";
            }
        }

        String amount = "";

        if (item.getCount() > 1) {
            amount = String.format("*%s", item.getCount());
        }

        return itemName + withNBT + amount;
    }
    
    public static String parseLiquidName(FluidStack fluid) {
        String liquidName = "<liquid:" + FluidRegistry.getFluidName(fluid.getFluid()) + ">";
        NBTTagCompound nbt = new NBTTagCompound();
        fluid.writeToNBT(nbt);
        String amount = "";
        if (fluid.amount > 0) {
            amount = String.format("*%s", fluid.amount);
        }
        String withNBT = "";
        if (nbt.hasKey("Tag")) {
             String nbt_data = NBTConverter.from(nbt.getTag("Tag"), false).toString();
            if (nbt_data.length() > 0) {
                withNBT = ".withTag(" + nbt_data + ")";
            }
        }
        return liquidName + withNBT + amount;
    }
    
    public static void registerCommands() {
        CTChatCommand.registerCommand(new CraftTweakerCommand("thermalRemovals") {

            protected void init() {
                this.setDescription(new ITextComponent[] { SpecialMessagesChat.getClickableCommandText("§2/ct thermalRemovals", "/ct thermalRemovals", true), SpecialMessagesChat.getNormalMessage(" §3Creates recipe removal functions for all Thermal Expansion machines in the crafttweaker log") });
            }
            
            public void executeCommand(MinecraftServer server,  ICommandSender sender,  String[] args) {
                CraftTweakerAPI.logCommand("# Centrifuge (non-mob recipes)");
                for(CentrifugeManager.CentrifugeRecipe recipe : CentrifugeManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Centrifuge.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Centrifuge (mob recipes)");
                for(CentrifugeManager.CentrifugeRecipe recipe : CentrifugeManager.getRecipeListMobs()) {
                    if (recipe.getInput().getMetadata() == 0) { // as removal is via entityId, ignore reusable morbs
                        CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Centrifuge.removeRecipeMob(<entity:%s>);", recipe.getInput().getTagCompound().getString("id")));
                    }
                }
                CraftTweakerAPI.logCommand("# Compactor PLATE mode recipes");
                for(CompactorManager.CompactorRecipe recipe : CompactorManager.getRecipeList(CompactorManager.Mode.PLATE)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Compactor.removeStorageRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Compactor COIN mode recipes");
                for(CompactorManager.CompactorRecipe recipe : CompactorManager.getRecipeList(CompactorManager.Mode.COIN)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Compactor.removeMintRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Compactor GEAR mode recipes");
                for(CompactorManager.CompactorRecipe recipe : CompactorManager.getRecipeList(CompactorManager.Mode.GEAR)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Compactor.removeGearRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Compactor ALL mode recipes");
                for(CompactorManager.CompactorRecipe recipe : CompactorManager.getRecipeList(CompactorManager.Mode.ALL)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Compactor.removePressRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Crucible recipes:");
                for(CrucibleManager.CrucibleRecipe recipe : CrucibleManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Crucible.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Enchanter recipes:");
                for(EnchanterManager.EnchanterRecipe recipe : EnchanterManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Enchanter.removeRecipe(%s, %s);", ThermalExpansion.parseItemName(recipe.getPrimaryInput()), ThermalExpansion.parseItemName(recipe.getSecondaryInput())));
                }
                CraftTweakerAPI.logCommand("# Induction Smelter recipes:");
                for(SmelterManager.SmelterRecipe recipe : SmelterManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.InductionSmelter.removeRecipe(%s, %s);", ThermalExpansion.parseItemName(recipe.getPrimaryInput()), ThermalExpansion.parseItemName(recipe.getSecondaryInput())));
                }
                CraftTweakerAPI.logCommand("# Infuser recipes:");
                for(ChargerManager.ChargerRecipe recipe : ChargerManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Infuser.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Pulverizer recipes:");
                for(PulverizerManager.PulverizerRecipe recipe : PulverizerManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Pulverizer.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Redstone Furnace recipe (followed by Pyrolysis recipes)");
                for(FurnaceManager.FurnaceRecipe recipe : FurnaceManager.getRecipeList(false)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.RedstoneFurnace.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                for(FurnaceManager.FurnaceRecipe recipe : FurnaceManager.getRecipeList(true)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.RedstoneFurnace.removePyrolysisRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Sawmill recipes:");
                for(SawmillManager.SawmillRecipe recipe : SawmillManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Sawmill.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                CraftTweakerAPI.logCommand("# Transposer recipes:");
                for(TransposerManager.TransposerRecipe recipe : TransposerManager.getExtractRecipeList()) {
                     ItemStack item = recipe.getInput();
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Transposer.removeExtractRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                for(TransposerManager.TransposerRecipe recipe : TransposerManager.getFillRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Transposer.removeFillRecipe(%s, %s);", ThermalExpansion.parseItemName(recipe.getInput()), ThermalExpansion.parseLiquidName(recipe.getFluid())));
                }
                CraftTweakerAPI.logCommand("# Imbuer recipes:");
                for(BrewerManager.BrewerRecipe recipe : BrewerManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Imbuer.removeRecipe(%s, %s);", ThermalExpansion.parseItemName(recipe.getInput()), ThermalExpansion.parseLiquidName(recipe.getInputFluid())));
                }
                CraftTweakerAPI.logCommand("# Refinery recipes:");
                for(RefineryManager.RefineryRecipe recipe : RefineryManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Refinery.removeRecipe(%s);", ThermalExpansion.parseLiquidName(recipe.getInput())));
                }
                for(RefineryManager.RefineryRecipe recipe : RefineryManager.getRecipeListPotion()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Refinery.removeRecipePotion(%s);", ThermalExpansion.parseLiquidName(recipe.getInput())));
                }
                for(InsolatorManager.InsolatorRecipe recipe : InsolatorManager.getRecipeList()) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Insolator.removeRecipe(%s, %s);", ThermalExpansion.parseItemName(recipe.getPrimaryInput()), ThermalExpansion.parseItemName(recipe.getSecondaryInput())));
                }
                for(FactorizerManager.FactorizerRecipe recipe : FactorizerManager.getRecipeList(false)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Factorizer.removeRecipe(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }
                for(FactorizerManager.FactorizerRecipe recipe : FactorizerManager.getRecipeList(true)) {
                    CraftTweakerAPI.logCommand(String.format("mods.thermalexpansion.Factorizer.removeRecipeReverse(%s);", ThermalExpansion.parseItemName(recipe.getInput())));
                }


                sender.sendMessage(SpecialMessagesChat.getLinkToCraftTweakerLog("Thermal Expansion removal recipes generated", sender));
            }
        });
    }
}
