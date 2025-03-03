/*
 * This is the latest source code of TNT Breaks Bedrock.
 * Minecraft version: 1.19.2, mod version: 2.5.
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

package com.natamus.tntbreaksbedrock;

import com.natamus.collective_fabric.check.RegisterMod;
import com.natamus.collective_fabric.fabric.callbacks.CollectiveExplosionEvents;
import com.natamus.tntbreaksbedrock.events.BoomEvent;
import com.natamus.tntbreaksbedrock.util.Reference;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() { 
		registerEvents();
		
		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}
	
	private void registerEvents() {
		CollectiveExplosionEvents.EXPLOSION_DETONATE.register((Level world, Entity sourceEntity, Explosion explosion) -> {
			BoomEvent.onExplosion(world, sourceEntity, explosion);
		});
	}
}
