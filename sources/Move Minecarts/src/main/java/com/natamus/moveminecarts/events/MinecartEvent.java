/*
 * This is the latest source code of Move Minecarts.
 * Minecraft version: 1.19.2, mod version: 2.0.
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

package com.natamus.moveminecarts.events;

import com.natamus.moveminecarts.config.ConfigHandler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent.MouseButton.Post;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MinecartEvent {
	private static Entity pickedupminecart = null;
	private static boolean rmbdown = false;
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent e) {
		if (pickedupminecart == null) {
			return;
		}
		
		Player player = e.player;
		Level world = player.getCommandSenderWorld();
		if (world.isClientSide || e.phase != Phase.START) {
			return;
		}
		
		for (Entity passenger : pickedupminecart.getPassengers()) {
			if (passenger.is(player)) {
				pickedupminecart = null;
				return;
			}
		}
		
		Vec3 look = player.getLookAngle();
		float distance = 2.0F;
		double dx = player.getX() + (look.x * distance);
		double dy = player.getY() + player.getEyeHeight();
		double dz = player.getZ() + (look.z * distance);
		pickedupminecart.setPos(dx, dy, dz);
	}
	
	@SubscribeEvent
	public static void onMinecartClick(PlayerInteractEvent.EntityInteract e) {
		if (!e.getTarget().getClass().getName().toLowerCase().contains(".minecart")) {
			return;
		}
		Level world = e.getLevel();
		if (world.isClientSide) {
			return;
		}
		if (ConfigHandler.GENERAL.mustSneakToPickUp.get()) {
			if (!e.getEntity().isCrouching()) {
				return;
			}
		}
		
		if (rmbdown) {
			e.setCanceled(true);
			pickedupminecart = (Entity)e.getTarget();
		}
		else if (pickedupminecart != null) {
			e.setCanceled(true);
			pickedupminecart = null;
		}
	}
	
	@SubscribeEvent
	public static void onMouseEvent(Post e) {
		if (e.getButton() != 1) return;
		if (!rmbdown) {
			if (pickedupminecart != null) {
				pickedupminecart = null;
			}
			rmbdown = true;
		} else {
			rmbdown = false;
		}
	}
}
