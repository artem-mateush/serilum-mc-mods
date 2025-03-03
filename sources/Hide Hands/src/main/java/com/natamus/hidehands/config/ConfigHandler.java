/*
 * This is the latest source code of Hide Hands.
 * Minecraft version: 1.19.2, mod version: 1.7.
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

package com.natamus.hidehands.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final General GENERAL = new General(BUILDER);
	public static final ForgeConfigSpec spec = BUILDER.build();

	public static class General {
		public final ForgeConfigSpec.ConfigValue<Boolean> alwaysHideMainHand;
		public final ForgeConfigSpec.ConfigValue<String> hideMainHandWithItems;
		public final ForgeConfigSpec.ConfigValue<Boolean> alwaysHideOffhand;
		public final ForgeConfigSpec.ConfigValue<String> hideOffhandWithItems;

		public General(ForgeConfigSpec.Builder builder) {
			builder.push("General");
			alwaysHideMainHand = builder
					.comment("If enabled, always hides the main hand. If disabled, hides the main hand when holding the items defined in hideMainHandWithItems.")
					.define("alwaysHideMainHand", false);
			hideMainHandWithItems = builder
					.comment("The items which when held will hide the main hand if alwaysHideMainHand is disabled.")
					.define("hideMainHandWithItems", "");
			
			alwaysHideOffhand = builder
					.comment("If enabled, always hides the offhand. If disabled, hides the offhand when holding the items defined in hideOffhandWithItems.")
					.define("alwaysHideOffhand", false);
			hideOffhandWithItems = builder
					.comment("The items which when held will hide the offhand if alwaysHideOffhand is disabled.")
					.define("hideOffhandWithItems", "minecraft:totem_of_undying,minecraft:torch");
			
			builder.pop();
		}
	}
}