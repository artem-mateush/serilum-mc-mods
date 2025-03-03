/*
 * This is the latest source code of Easy Elytra Takeoff.
 * Minecraft version: 1.19.2, mod version: 3.0.
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

package com.natamus.easyelytratakeoff.events;

import com.natamus.collective.functions.EntityFunctions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Collection;
import java.util.HashMap;

@EventBusSubscriber
public class ElytraEvent {
	private static HashMap<String, Integer> newrockets = new HashMap<String, Integer>();

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		Player player = e.player;
		Level level = player.level;
		if (level.isClientSide) {
			return;
		}

		String playerName = player.getName().getString();
		if (newrockets.containsKey(playerName)) {
			int left = newrockets.get(playerName);
			if (left > -5) {
				if (left > 0) {
					EntityFunctions.setEntityFlag(player, 7, true);
					FireworkRocketEntity efr = new FireworkRocketEntity(level, player.getItemInHand(InteractionHand.MAIN_HAND), player);
					level.addFreshEntity(efr);
				}

				EntityFunctions.setEntityFlag(player, 7, true);
				newrockets.put(playerName, left-1);
			}
		}
	}
	
	@SubscribeEvent
	public void onFirework(PlayerInteractEvent.RightClickItem e) {
		Level world = e.getLevel();
		if (world.isClientSide) {
			return;
		}
		
		ItemStack hand = e.getItemStack();
		if (!hand.getItem().equals(Items.FIREWORK_ROCKET)) {
			return;
		}
		
		Player player = e.getEntity();
		if (player.isFallFlying()) {
			return;
		}

		BlockPos belowpos = player.blockPosition().below();

		boolean inAir = true;
		for (BlockPos next : BlockPos.betweenClosed(belowpos.getX() - 1, belowpos.getY() - 1, belowpos.getZ() - 1, belowpos.getX() + 1, belowpos.getY() - 1, belowpos.getZ() + 1)) {
			Block nextblock = world.getBlockState(next).getBlock();
			if (!nextblock.equals(Blocks.AIR)) {
				inAir = false;
				break;
			}
		}

		if (inAir) {
			return;
		}

		boolean foundelytra = false;
		for (ItemStack nis : player.getArmorSlots()) {
			if (nis.getItem() instanceof ElytraItem) {
				foundelytra = true;
				break;
			}
		}
		
		if (!foundelytra) {
			Collection<AttributeInstance> atrb = player.getAttributes().getSyncableAttributes();
			for (AttributeInstance ai : atrb) {
				for (AttributeModifier m : ai.getModifiers()) {
					String name = m.getName().toLowerCase();
					if (name.equals("flight modifier") || name.equals("elytra curio modifier")) {
						if (m.getAmount() >= 1.0) {
							foundelytra = true;
							break;
						}
					}
				}
				if (foundelytra) {
					break;
				}
			}
			if (!foundelytra) {
				return;
			}
		}

		String playerName = player.getName().getString();
		newrockets.put(playerName, 1);

		if (!player.isCreative()) {
			hand.shrink(1);
		}
	}
}
