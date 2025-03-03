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

package com.natamus.thevanillaexperience.mods.endportalrecipe.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.primitives.Ints;
import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.functions.ItemFunctions;
import com.natamus.collective.functions.ToolFunctions;
import com.natamus.thevanillaexperience.mods.endportalrecipe.config.EndPortalRecipeConfigHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EndPortalRecipeEndPortalEvent {
	@SubscribeEvent
	public void mobItemDrop(LivingDropsEvent e) {
		Entity entity = e.getEntity();
		Level world = entity.getCommandSenderWorld();
		if (world.isClientSide) {
			return;
		}
		
		if (entity instanceof EnderDragon == false) {
			return;
		}
		
		Entity source = e.getSource().getEntity();
		if (entity instanceof Player == false) {
			return;
		}

		Player player = (Player)source;
		ItemStack egg = new ItemStack(Blocks.DRAGON_EGG, 1);
		ItemFunctions.giveOrDropItemStack(player, egg);
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
		Level world = e.getWorld();
		if (world.isClientSide) {
			return;
		}
		
		ItemStack itemstack = e.getItemStack();
		if (itemstack.getItem().equals(Items.FLINT_AND_STEEL)) {
			int aircount = 0;
			BlockPos centerpos = null;
			
			BlockPos cpos = e.getPos();
			Iterator<BlockPos> it = BlockPos.betweenClosedStream(cpos.getX()-1, cpos.getY()+1, cpos.getZ()-1, cpos.getX()+1, cpos.getY()+1, cpos.getZ()+1).iterator();
			while (it.hasNext()) {
				aircount = 0;
				BlockPos np = it.next();
				Iterator<BlockPos> npit = BlockPos.betweenClosedStream(np.getX()-1, np.getY(), np.getZ()-1, np.getX()+1, np.getY(), np.getZ()+1).iterator();
				while (npit.hasNext()) {
					BlockPos npp = npit.next();
					Block block = world.getBlockState(npp).getBlock();
					if (block.equals(Blocks.AIR) || block.equals(Blocks.FIRE)) {
						if (aircount == 4) {
							centerpos = npp.immutable();
						}
						aircount++;
					}
				}
				if (aircount == 9) {
					break;
				}
				aircount++;
			}
			
			if (aircount != 9) {
				return;
			}
			
			if (centerpos != null) {
				//       1 2 3 
				//    5         9
				//   10         14
				//   15         19
				//     21 22 23
				List<Integer> portalnumbers = new ArrayList<Integer>(Ints.asList(1, 2, 3, 5, 9, 10, 14, 15, 19, 21, 22, 23));
				
				int pi = 0;
				Iterator<BlockPos> centerit = BlockPos.betweenClosedStream(centerpos.getX()-2, centerpos.getY(), centerpos.getZ()-2, centerpos.getX()+2, centerpos.getY(), centerpos.getZ()+2).iterator();
				while (centerit.hasNext()) {
					BlockPos ncp = centerit.next();
					if (portalnumbers.contains(pi)) {
						BlockState ibs = world.getBlockState(ncp);
						if (ibs.getBlock().equals(Blocks.END_PORTAL_FRAME)) {
							if (!BlockFunctions.isFilledPortalFrame(ibs)) {
								break;
							}
						}
						else {
							break;
						}
					}
					pi++;
				}
				
				if (pi == 25) {
					Iterator<BlockPos> portalposses = BlockPos.betweenClosedStream(centerpos.getX()-1, centerpos.getY(), centerpos.getZ()-1, centerpos.getX()+1, centerpos.getY(), centerpos.getZ()+1).iterator();
					while (portalposses.hasNext()) {
						BlockPos ppp = portalposses.next();
						world.setBlockAndUpdate(ppp, Blocks.END_PORTAL.defaultBlockState());
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickBlock e) {
		Level world = e.getWorld();
		if (world.isClientSide) {
			return;
		}
		
		ItemStack hand = e.getItemStack();
		if (ToolFunctions.isPickaxe(hand)) {
			if (EndPortalRecipeConfigHandler.GENERAL.mustHaveSilkTouchToBreakPortal.get()) {
				if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, hand) < 1) {
					return;
				}
			}
			
			BlockPos cpos = e.getPos().immutable();
			BlockState cbs = world.getBlockState(cpos);
			if (cbs.getBlock().equals(Blocks.END_PORTAL_FRAME)) {
				Player player = e.getPlayer();
				ItemStack portalframe = new ItemStack(Blocks.END_PORTAL_FRAME, 1);
				ItemStack endereye = new ItemStack(Items.ENDER_EYE, 1);
				
				if (EndPortalRecipeConfigHandler.GENERAL.addBrokenPortalFramesToInventory.get()) {
					ItemFunctions.giveOrDropItemStack(player, portalframe);
					if (BlockFunctions.isFilledPortalFrame(cbs)) {
						ItemFunctions.giveOrDropItemStack(player, endereye);
					}
				}
				else {
					ItemEntity frame = new ItemEntity(world, cpos.getX(), cpos.getY()+1, cpos.getZ(), portalframe);
					world.addFreshEntity(frame);
					
					if (BlockFunctions.isFilledPortalFrame(cbs)) {
						ItemEntity eoe = new ItemEntity(world, cpos.getX(), cpos.getY()+1, cpos.getZ(), endereye);
						world.addFreshEntity(eoe);
					}
				}
				
				world.destroyBlock(cpos, false);
				Iterator<BlockPos> it = BlockPos.betweenClosedStream(cpos.getX()-3,  cpos.getY(), cpos.getZ()-3, cpos.getX()+3, cpos.getY(), cpos.getZ()+3).iterator();
				while (it.hasNext()) {
					BlockPos np = it.next();
					if (world.getBlockState(np).getBlock().equals(Blocks.END_PORTAL)) {
						world.setBlockAndUpdate(np, Blocks.AIR.defaultBlockState());
					}
				}
			}
		}
	}
}
