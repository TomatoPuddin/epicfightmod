package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class DealtDamageEvent<T extends PlayerPatch<?>> extends PlayerEvent<T> {
	private float attackDamage;
	private LivingEntity target;
	private EpicFightDamageSource damageSource;
	
	public DealtDamageEvent(T playerpatch, LivingEntity target, EpicFightDamageSource source, float damage) {
		super(playerpatch, false);
		this.target = target;
		this.damageSource = source;
		this.attackDamage = damage;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public EpicFightDamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}
	
	public float getAttackDamage() {
		return this.attackDamage;
	}
}