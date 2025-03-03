/*
 * This is the latest source code of Configurable Extra Mob Drops.
 * Minecraft version: 1.19.2, mod version: 2.1.
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

package com.natamus.configurableextramobdrops.events;

import java.util.concurrent.CopyOnWriteArrayList;

import com.natamus.collective.data.GlobalVariables;
import com.natamus.configurableextramobdrops.util.Util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class MobDropEvent {
	@SubscribeEvent
	public void mobItemDrop(LivingDropsEvent e) {
		Entity entity = e.getEntity();
		Level world = entity.getCommandSenderWorld();
		if (world.isClientSide) {
			return;
		}
		
		EntityType<?> entitytype = entity.getType();
		if (!Util.mobdrops.containsKey(entitytype)) {
			return;
		}
		
		CopyOnWriteArrayList<ItemStack> extradrops = Util.mobdrops.get(entitytype);
		if (extradrops.size() > 0) {
			BlockPos epos = entity.blockPosition();
			
			for (ItemStack itemstack : extradrops) {
				ItemStack newstack = itemstack.copy();
				CompoundTag tag = newstack.getOrCreateTag();
				
				CompoundTag nbt = new CompoundTag();
				newstack.save(nbt);
				
				
				if (tag.contains("dropchance")) {
					double dropchance = tag.getDouble("dropchance");
					if (dropchance != 1.0) {
						double chanceroll = GlobalVariables.random.nextDouble();
						if (chanceroll > dropchance) {
							continue;
						}
					}
					tag.remove("dropchance");
					if (tag.size() == 0) {
						nbt.remove("tag");
						newstack = ItemStack.of(nbt);
					}
					else {
						newstack.setTag(tag);
					}
				}
				
				e.getDrops().add(new ItemEntity(world, epos.getX(), epos.getY()+1, epos.getZ(), newstack.copy()));
			}
		}
	}
}
