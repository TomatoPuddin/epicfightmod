package yesman.epicfight.world.damagesource;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class IndirectEpicFightDamageSource extends IndirectEntityDamageSource implements EpicFightDamageSource {
	private DamageSourceElements damageSourceElements;
	private Vec3 projectileInitialPosition;
	
	public IndirectEpicFightDamageSource(String damageTypeIn, Entity owner, Entity projectile, StunType stunType) {
		super(damageTypeIn, projectile, owner);
		
		this.damageSourceElements = new DamageSourceElements();
		this.damageSourceElements.stunType = stunType;
	}
	
	@Override
	public void setInitialPosition(Vec3 initialPosition) {
		this.projectileInitialPosition = initialPosition;
	}
	
	@Override
	public Vec3 getSourcePosition() {
		return this.projectileInitialPosition != null ? this.projectileInitialPosition : super.getSourcePosition();
	}
	
	@Override
	public boolean isBasicAttack() {
		return false;
	}
	
	@Override
	public int getAnimationId() {
		return -1;
	}
	
	@Override
	public DamageSourceElements getDamageSourceElements() {
		return this.damageSourceElements;
	}
}