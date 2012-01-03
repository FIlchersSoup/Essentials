package com.earth2me.essentials.protect;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.craftbukkit.FakeExplosion;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.*;


public class EssentialsProtectEntityListener extends EntityListener
{
	private final transient IProtect prot;
	private final transient IEssentials ess;

	public EssentialsProtectEntityListener(final IProtect prot)
	{
		this.prot = prot;
		this.ess = prot.getEssentialsConnect().getEssentials();
	}

	@Override
	public void onEntityDamage(final EntityDamageEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final Entity target = event.getEntity();

		if (target instanceof Villager && prot.getSettingBool(ProtectConfig.prevent_villager_death))
		{
			event.setCancelled(true);
			return;
		}

		final IUser user = target instanceof Player ? ess.getUser((Player)target) : null;
		if (event instanceof EntityDamageByBlockEvent)
		{
			final DamageCause cause = event.getCause();

			if (prot.getSettingBool(ProtectConfig.disable_contactdmg)
				&& cause == DamageCause.CONTACT
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.contact")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (prot.getSettingBool(ProtectConfig.disable_lavadmg)
				&& cause == DamageCause.LAVA
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.lava")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
			if (prot.getSettingBool(ProtectConfig.prevent_tnt_explosion)
				&& cause == DamageCause.BLOCK_EXPLOSION
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.tnt")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}
		}

		if (event instanceof EntityDamageByEntityEvent)
		{
			final EntityDamageByEntityEvent edEvent = (EntityDamageByEntityEvent)event;
			final Entity eAttack = edEvent.getDamager();
			final IUser attacker = eAttack instanceof Player ? ess.getUser((Player)eAttack) : null;

			// PVP Settings
			if (target instanceof Player && eAttack instanceof Player
				&& prot.getSettingBool(ProtectConfig.disable_pvp)
				&& (!user.isAuthorized("essentials.protect.pvp") || !attacker.isAuthorized("essentials.protect.pvp")))
			{
				event.setCancelled(true);
				return;
			}

			//Creeper explode prevention
			if (eAttack instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_explosion)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.creeper")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}

			if (eAttack instanceof Creeper && prot.getSettingBool(ProtectConfig.prevent_creeper_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.creeper")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}

			if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
				&& prot.getSettingBool(ProtectConfig.prevent_fireball_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.fireball")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}

			if (eAttack instanceof TNTPrimed && prot.getSettingBool(ProtectConfig.prevent_tnt_playerdmg)
				&& !(target instanceof Player
					 && user.isAuthorized("essentials.protect.damage.tnt")
					 && !user.isAuthorized("essentials.protect.damage.disable")))
			{
				event.setCancelled(true);
				return;
			}

			if (edEvent.getDamager() instanceof Projectile
				&& target instanceof Player
				&& ((prot.getSettingBool(ProtectConfig.disable_projectiles)
					 && !(user.isAuthorized("essentials.protect.damage.projectiles")
						  && !user.isAuthorized("essentials.protect.damage.disable")))
					|| (((Projectile)edEvent.getDamager()).getShooter() instanceof Player
						&& prot.getSettingBool(ProtectConfig.disable_pvp)
						&& (!user.isAuthorized("essentials.protect.pvp")
							|| !ess.getUser((Player)((Projectile)edEvent.getDamager()).getShooter()).isAuthorized("essentials.protect.pvp")))))
			{
				event.setCancelled(true);
				return;
			}
		}

		final DamageCause cause = event.getCause();
		if (target instanceof Player)
		{
			if (cause == DamageCause.FALL
				&& (user.isAuthorized(Permissions.PREVENTDAMAGE_FALL)
					&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
			{
				event.setCancelled(true);
				return;
			}

			if (cause == DamageCause.SUFFOCATION
				&& (user.isAuthorized(Permissions.PREVENTDAMAGE_SUFFOCATION)
					&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
			{
				event.setCancelled(true);
				return;
			}
			if ((cause == DamageCause.FIRE
				 || cause == DamageCause.FIRE_TICK)
				&& (user.isAuthorized(Permissions.PREVENTDAMAGE_FIRE)
					&& !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
			{
				event.setCancelled(true);
				return;
			}
			if (cause == DamageCause.DROWNING
				&& (user.isAuthorized(Permissions.PREVENTDAMAGE_DROWNING)
					 && !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
			{
				event.setCancelled(true);
				return;
			}
			if (cause == DamageCause.LIGHTNING
				&& (user.isAuthorized(Permissions.PREVENTDAMAGE_LIGHTNING)
					 && !user.isAuthorized(Permissions.PREVENTDAMAGE_NONE)))
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void onEntityExplode(final EntityExplodeEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final int maxHeight = ess.getSettings().getProtectCreeperMaxHeight();

		if (event.getEntity() instanceof EnderDragon
			&& prot.getSettingBool(ProtectConfig.prevent_enderdragon_blockdmg))
		{
			if (prot.getSettingBool(ProtectConfig.enderdragon_fakeexplosions))
			{
				FakeExplosion.createExplosion(event, ess.getServer(), ess.getServer().getOnlinePlayers());
			}
			event.setCancelled(true);
			return;
		}
		else if (event.getEntity() instanceof Creeper
				 && (prot.getSettingBool(ProtectConfig.prevent_creeper_explosion)
					 || prot.getSettingBool(ProtectConfig.prevent_creeper_blockdmg)
					 || (maxHeight >= 0 && event.getLocation().getBlockY() > maxHeight)))
		{
			//Nicccccccccce plaaacccccccccce..
			FakeExplosion.createExplosion(event, ess.getServer(), ess.getServer().getOnlinePlayers());
			event.setCancelled(true);
			return;
		}
		else if (event.getEntity() instanceof TNTPrimed
				 && prot.getSettingBool(ProtectConfig.prevent_tnt_explosion))
		{
			event.setCancelled(true);
			return;
		}
		else if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
				 && prot.getSettingBool(ProtectConfig.prevent_fireball_explosion))
		{
			event.setCancelled(true);
			return;
		}
		// This code will prevent explosions near protected rails, signs or protected chests
		// TODO: Use protect db instead of this code

		for (Block block : event.blockList())
		{
			if ((block.getRelative(BlockFace.UP).getType() == Material.RAILS
				 || block.getType() == Material.RAILS
				 || block.getRelative(BlockFace.UP).getType() == Material.POWERED_RAIL
				 || block.getType() == Material.POWERED_RAIL
				 || block.getRelative(BlockFace.UP).getType() == Material.DETECTOR_RAIL
				 || block.getType() == Material.DETECTOR_RAIL)
				&& prot.getSettingBool(ProtectConfig.protect_rails))
			{
				event.setCancelled(true);
				return;
			}
			if ((block.getType() == Material.WALL_SIGN
				 || block.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN
				 || block.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN
				 || block.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN
				 || block.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN
				 || block.getType() == Material.SIGN_POST
				 || block.getRelative(BlockFace.UP).getType() == Material.SIGN_POST)
				&& prot.getSettingBool(ProtectConfig.protect_signs))
			{
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void onCreatureSpawn(final CreatureSpawnEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			return;
		}
		if (event.isCancelled())
		{
			return;
		}
		final CreatureType creature = event.getCreatureType();
		if (creature == null)
		{
			return;
		}
		final String creatureName = creature.toString().toLowerCase(Locale.ENGLISH);
		if (creatureName == null || creatureName.isEmpty())
		{
			return;
		}
		if (ess.getSettings().getProtectPreventSpawn(creatureName))
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onEntityTarget(final EntityTargetEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (!(event.getTarget() instanceof Player))
		{
			return;
		}
		final IUser user = ess.getUser((Player)event.getTarget());
		if ((event.getReason() == TargetReason.CLOSEST_PLAYER
			 || event.getReason() == TargetReason.TARGET_ATTACKED_ENTITY
			 || event.getReason() == TargetReason.PIG_ZOMBIE_TARGET
			 || event.getReason() == TargetReason.RANDOM_TARGET
			 || event.getReason() == TargetReason.TARGET_ATTACKED_OWNER
			 || event.getReason() == TargetReason.OWNER_ATTACKED_TARGET)
			&& prot.getSettingBool(ProtectConfig.prevent_entitytarget)
			&& !user.isAuthorized("essentials.protect.entitytarget.bypass"))
		{
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onExplosionPrime(ExplosionPrimeEvent event)
	{
		if ((event.getEntity() instanceof Fireball || event.getEntity() instanceof SmallFireball)
			&& prot.getSettingBool(ProtectConfig.prevent_fireball_fire))
		{
			event.setFire(false);
		}
	}

	@Override
	public void onEndermanPickup(EndermanPickupEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (prot.getSettingBool(ProtectConfig.prevent_enderman_pickup))
		{
			event.setCancelled(true);
			return;
		}
	}
}
