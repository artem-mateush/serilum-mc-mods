/*
 * This is the latest source code of Configurable Furnace Burn Time.
 * Minecraft version: 1.19.2, mod version: 1.6.
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

package com.natamus.configurablefurnaceburntime.events;

import com.natamus.configurablefurnaceburntime.config.ConfigHandler;

import net.minecraft.world.item.ItemStack;

public class FurnaceBurnEvent {
	public static int furnaceBurnTimeEvent(ItemStack itemStack, int burntime) {
		return (int)Math.ceil(burntime * ConfigHandler.burnTimeModifier);
	}
}
