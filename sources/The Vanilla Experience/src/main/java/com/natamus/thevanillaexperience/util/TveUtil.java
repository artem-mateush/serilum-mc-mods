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

package com.natamus.thevanillaexperience.util;

import java.util.List;

import com.natamus.collective.functions.DataFunctions;

public class TveUtil {
	private static List<String> installedmodjars = DataFunctions.getInstalledModJars();
	
	public static boolean shouldLoadMod(String modname) {
		String jarpart = modname.toLowerCase().replaceAll(" ", "");
		for (String modjar : installedmodjars) {
			if (modjar.equalsIgnoreCase(jarpart + "_")) {
				System.out.println("Disabled " + modname + " in The Vanilla Experience mod because it already exists as a separate mod.");
				return false;
			}
		}
		
		return true;
	}
}
