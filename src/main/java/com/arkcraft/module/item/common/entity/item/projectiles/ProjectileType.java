package com.arkcraft.module.item.common.entity.item.projectiles;

public enum ProjectileType
{
	SIMPLE_BULLET("EntitySimpleBullet"),
	SIMPLE_RIFLE_AMMO("EntitySimpleBullet"),
	SIMPLE_SHOTGUN_AMMO("EntitySimpleBullet"),
	TRANQUILIZER("EntityTranquilizer"),
	ROCKET_PROPELLED_GRENADE("EntityRocketPropelledGrenade"),
	ADVANCED_BULLET("EntityAdvancedBullet"),
	METAL_ARROW("EntityMetalArrow"),
	STONE_ARROW("EntityStoneArrow"),
	TRANQ_ARROW("EntityTranqArrow");

	String entity;

	ProjectileType(String entity)
	{
		this.entity = entity;
	}

	public String getEntity()
	{
		return entity;
	}
}
