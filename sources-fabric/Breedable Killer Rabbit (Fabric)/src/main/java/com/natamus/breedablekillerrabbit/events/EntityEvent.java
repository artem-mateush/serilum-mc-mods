/*
 * This is the latest source code of Breedable Killer Rabbit.
 * Minecraft version: 1.19.2, mod version: 2.2.
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

package com.natamus.breedablekillerrabbit.events;

import java.util.List;

import com.natamus.breedablekillerrabbit.config.ConfigHandler;
import com.natamus.collective_fabric.data.GlobalVariables;
import com.natamus.collective_fabric.functions.StringFunctions;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class EntityEvent {
	public static boolean onBaby(ServerLevel world, Animal parentA, Animal parentB, AgeableMob offspring) {
		if (offspring instanceof Rabbit == false) {
			return true;
		}
		Rabbit rabbit = (Rabbit)offspring;
		
		double num = GlobalVariables.random.nextDouble();
		if (num <= ConfigHandler.chanceBabyRabbitIsKiller) {
			rabbit.setRabbitType(99);
			if (ConfigHandler.removeKillerRabbitNameTag) {
				rabbit.setCustomName(null);
			}
			
			Vec3 vec = offspring.position();
			List<Entity> entitiesaround = world.getEntities(null, new AABB(vec.x-10, vec.y-10, vec.z-10, vec.x+10, vec.y+10, vec.z+10));
			for (Entity entityaround : entitiesaround) {
				if (entityaround instanceof Player) {
					Player player = (Player)entityaround;
					StringFunctions.sendMessage(player, "A killer rabbit has been born! Are you far enough away or do you have a golden carrot to share?", ChatFormatting.DARK_GREEN);
					return true;
				}
			}
		}
		
		return true;
	}
	
	public static InteractionResult onEntityInteract(Player player, Level world, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
		if (world.isClientSide) {
			return InteractionResult.PASS;
		}
		
		if (entity instanceof Rabbit == false) {
			return InteractionResult.PASS;
		}
		
		ItemStack itemstack = player.getItemInHand(hand);
		
		if (!itemstack.getItem().equals(Items.GOLDEN_CARROT)) {
			return InteractionResult.PASS;
		}
		
		Rabbit rabbit = (Rabbit)entity;
		if (rabbit.getRabbitType() != 99) {
			return InteractionResult.PASS;
		}
		if (rabbit.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.GOLDEN_CARROT)) {
			StringFunctions.sendMessage(player, "The killer rabbit has already been tamed.", ChatFormatting.DARK_GREEN);
			return InteractionResult.PASS;
		}
		
		rabbit.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_CARROT, 1));
		itemstack.shrink(1);
		StringFunctions.sendMessage(player, "The killer rabbit has been tamed!", ChatFormatting.DARK_GREEN);
		return InteractionResult.SUCCESS;
	}
	
	public static boolean onTarget(Level world, Entity entity, DamageSource damageSource, float damageAmount) {
		if (world.isClientSide) {
			return true;
		}
		
		Entity source = damageSource.getDirectEntity();
		if (source instanceof Rabbit == false) {
			return true;
		}
		
		if (((Rabbit)source).getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(Items.GOLDEN_CARROT)) {
			return false;
		}
		
		return true;
	}
	
	public static void mobSpawn(Level world, Entity entity) {
		if (world.isClientSide) {
			return;
		}
		
		if (entity instanceof Rabbit == false) {
			return;
		}
		if (!ConfigHandler.removeKillerRabbitNameTag) {
			return;
		}
		if (((Rabbit)entity).getRabbitType() != 99) {
			return;
		}
		if (!entity.hasCustomName()) {
			return;
		}
		
		if (entity.getCustomName().getString().equals("The Killer Bunny")) {
			entity.setCustomName(null);
		}
	}
	
	public static float onPlayerDamage(Level world, Entity entity, DamageSource damageSource, float damageAmount) {
		if (world.isClientSide) {
			return damageAmount;
		}
		
		if (entity instanceof Player == false) {
			return damageAmount;
		}
		
		Entity source = damageSource.getDirectEntity();
		if (source == null) {
			return damageAmount;
		}
		
		if (source instanceof Rabbit == false) {
			return damageAmount;
		}
		
		if (((Rabbit)source).getRabbitType() == 99) {
			StringFunctions.sendMessage((Player)entity, "The killer rabbit wants a golden carrot!", ChatFormatting.RED);
		}
		
		return damageAmount;
	}
}
