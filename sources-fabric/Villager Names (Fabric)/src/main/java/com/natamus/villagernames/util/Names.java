/*
 * This is the latest source code of Villager Names.
 * Minecraft version: 1.19.2, mod version: 4.2.
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

package com.natamus.villagernames.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.natamus.collective_fabric.data.GlobalVariables;
import com.natamus.collective_fabric.functions.StringFunctions;
import com.natamus.villagernames.config.ConfigHandler;

public class Names {
	public static List<String> customnames = null;
	public static String workspace_path = System.getProperty("user.dir");
	
	public static void setCustomNames() throws IOException {
		String dirpath = workspace_path + File.separator + "config" + File.separator + "villagernames";
		File dir = new File(dirpath);
		File file = new File(dirpath + File.separator + "customnames.txt");
		
		if (dir.isDirectory() && file.isFile()) {
			String cn = new String(Files.readAllBytes(Paths.get(dirpath + File.separator + "customnames.txt", new String[0])), "UTF-8");
			cn = cn.replace("\n", "").replace("\r", "");
			
			String[] cns = cn.split(",");
			customnames = Arrays.asList(cns);
		}
		else {
			dir.mkdirs();
			
			PrintWriter writer = new PrintWriter(dirpath + File.separator + "customnames.txt", "UTF-8");
			writer.println("Rick,");
			writer.println("Bob,");
			writer.println("Eve");
			writer.close();
			
			customnames = new ArrayList<>(Arrays.asList("Rick", "Bob", "Eve"));
		}
	}
	
	public static String getRandomName() {
		List<String> allnames = null;
		if (ConfigHandler._useFemaleNames && ConfigHandler._useMaleNames) {
			allnames = Stream.concat(GlobalVariables.femalenames.stream(), GlobalVariables.malenames.stream()).collect(Collectors.toList());
		}
		else if (ConfigHandler._useFemaleNames) {
			allnames = GlobalVariables.femalenames;
		}
		else if (ConfigHandler._useMaleNames) {
			allnames = GlobalVariables.malenames;
		}
		
		if (ConfigHandler._useCustomNames && customnames != null) {
			if (allnames == null) {
				allnames = customnames;
			}
			else {
				allnames = Stream.concat(customnames.stream(), allnames.stream()).collect(Collectors.toList());
			}
		}
		
		if (allnames == null) {
			return "";
		}
		
	    String name = allnames.get(GlobalVariables.random.nextInt(allnames.size())).toLowerCase();
	    return StringFunctions.capitalizeEveryWord(name);
	}
}
