/*
 * This is the latest source code of The Vanilla Experience.
 * Minecraft version: 1.17.1, mod version: 1.4.
 *
 * Please don't distribute without permission.
 * For all Minecraft modding projects, feel free to visit my profile page on CurseForge or Modrinth.
 *  CurseForge: https://curseforge.com/members/serilum/projects
 *  Modrinth: https://modrinth.com/user/serilum
 *  Overview: https://serilum.com/
 *
 * If you are feeling generous and would like to support the development of the mods, you can!
 *  https://ricksouth.com/donate contains all the information. <3
 *
 * Thanks for looking at the source code! Hope it's of some use to your project. Happy modding!
 */

package com.natamus.thevanillaexperience.mods.enchantingcommands.cmds;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.natamus.collective.functions.StringFunctions;
import com.natamus.thevanillaexperience.mods.enchantingcommands.config.EnchantingCommandsConfigHandler;
import com.natamus.thevanillaexperience.mods.enchantingcommands.util.EnchantingCommandsUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class CommandEc {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    	dispatcher.register(Commands.literal(EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get())
			.requires((iCommandSender) -> iCommandSender.hasPermission(2))
			.executes((command) -> {
				sendUsage(command.getSource());
				return 1;
			})
			.then(Commands.literal("list")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				String joined = String.join(", ", EnchantingCommandsUtil.enchantments());
				StringFunctions.sendMessage(source, "--- Enchanting Commands List ---", ChatFormatting.DARK_GREEN, true);
				StringFunctions.sendMessage(source, " " + joined, ChatFormatting.DARK_GREEN);
				return 1;
			}))
			.then(Commands.literal("enchant")
			.then(Commands.argument("enchantment", StringArgumentType.word())
			.then(Commands.argument("level", IntegerArgumentType.integer(0, 127))
			.executes((command) -> {
				CommandSourceStack source = command.getSource();
				Entity entity = source.getEntity();
				if (entity instanceof ServerPlayer == false) {
					StringFunctions.sendMessage(source, "This command can only be executed as a player.", ChatFormatting.RED);
					return 1;
				}
				
				Player player = (ServerPlayer)entity;
				ItemStack held = player.getMainHandItem();
				
				String enchantment = StringArgumentType.getString(command, "enchantment");
				Integer level = IntegerArgumentType.getInteger(command, "level");
				
				List<String> enchantments = EnchantingCommandsUtil.enchantments();
				if (!player.hasItemInSlot(EquipmentSlot.MAINHAND)) {
					StringFunctions.sendMessage(player, "You do not have an enchantable item in your main hand.", ChatFormatting.RED);
					return 0;
				}
				
				if (!enchantments.contains(enchantment.toLowerCase())) {
					StringFunctions.sendMessage(player, "The enchantment '" + enchantment + "' does not exist. See '/" + EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get() + " list' for the available enchantments.", ChatFormatting.RED);
					return 0;
				}
				
				@SuppressWarnings("deprecation")
				Enchantment enchant = Registry.ENCHANTMENT.byId(EnchantingCommandsUtil.getEnchantmentID(enchantment.toLowerCase()));
				
				ItemStack temp = new ItemStack(Item.byId(1));
				temp.enchant(enchant, level);
				String estringtemp = temp.getEnchantmentTags().get(0).toString().split("id:")[1];
				
				Boolean removed = false;
				for (Tag nbt : held.getEnchantmentTags()) {
					if (estringtemp.equals(nbt.toString().split("id:")[1])) {
						held.getEnchantmentTags().remove(nbt);
						removed = true;
						break;
					}
				}
				
				if (level != 0) {
					held.enchant(enchant, level);
					StringFunctions.sendMessage(player, "The enchantment '" + enchantment.toLowerCase() + "' has been added to the item with a level of " + level + ".", ChatFormatting.DARK_GREEN);
				}
				else if (removed) {
					StringFunctions.sendMessage(player, "The enchantment '" + enchantment.toLowerCase() + "' has been removed from the item.", ChatFormatting.DARK_GREEN);
				}
				else {
					StringFunctions.sendMessage(player, "The enchantment '" + enchantment.toLowerCase() + "' does not exist on the item.", ChatFormatting.RED);
				}
				return 1;
			}))))
		);
    }
    
	public static void sendUsage(CommandSourceStack source) {
		StringFunctions.sendMessage(source, "--- Enchanting Commands Usage ---", ChatFormatting.DARK_GREEN, true);
		StringFunctions.sendMessage(source, " /" + EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get() + " list", ChatFormatting.DARK_GREEN);
		StringFunctions.sendMessage(source, " /" + EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get() + " enchant <enchant> <lvl>", ChatFormatting.DARK_GREEN);
		return;
	}

	public static void sendUsage(Player player) {
		StringFunctions.sendMessage(player, "--- Enchanting Commands Usage ---", ChatFormatting.DARK_GREEN, true);
		StringFunctions.sendMessage(player, " /" + EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get() + " list", ChatFormatting.DARK_GREEN);
		StringFunctions.sendMessage(player, " /" + EnchantingCommandsConfigHandler.GENERAL.enchantCommandString.get() + " enchant <enchant> <lvl>", ChatFormatting.DARK_GREEN);
		return;
	}
}