package com.github.rumoteam.minecraft.savevillagers.init;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

	Plugin plugin;

	public DamageListener(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	void villagerDamage(EntityDamageEvent damageEvent) {
		Class<? extends EntityDamageEvent> cl = damageEvent.getClass();

		Entity entity = damageEvent.getEntity();

		if (entity.getType().equals(EntityType.VILLAGER)) {
			Villager villager = (Villager) entity;
			if (villager.getLocation().getY() <= -10) {
				villager.setHealth(0D);
				Collection<? extends Player> players = Bukkit.getOnlinePlayers();
				for (Player player : players) {
					if (player.isOp()) {
						int x = (int) villager.getLocation().getX();
						int y = (int) villager.getLocation().getY();
						int z = (int) villager.getLocation().getZ();
						player.sendMessage("Villager killed:" + x + ":" + y + ":" + z);
					}
				}
			} else {
				damageEvent.setCancelled(true);
				if (cl.equals(EntityDamageByEntityEvent.class)) {
					EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
					Entity damager = damageByEntityEvent.getDamager();
					double damage = damageEvent.getFinalDamage();

					if (damager instanceof LivingEntity) {
						LivingEntity damagerL = (LivingEntity) damager;
						damagerL.damage(damage);
					}
				}
			}

		}
	}
}
