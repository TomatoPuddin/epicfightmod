package yesman.epicfight.world.damagesource;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightEntityDamageSource extends EntityDamageSource implements EpicFightDamageSource {
	private DamageSourceElements damageSourceElements;
	private final StaticAnimation animation;
	private Vec3 initialPosition;
	
	public EpicFightEntityDamageSource(String msgId, Entity owner, StunType stunType, StaticAnimation animation) {
		this(msgId, owner, stunType, animation, InteractionHand.MAIN_HAND);
	}
	
	public EpicFightEntityDamageSource(String msgId, Entity owner, StunType stunType, StaticAnimation animation, InteractionHand hand) {
		super(msgId, owner);
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) owner.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		this.damageSourceElements = new DamageSourceElements();
		this.damageSourceElements.stunType = stunType;
		this.damageSourceElements.impact = entitypatch.getImpact(hand);
		this.damageSourceElements.armorNegation = entitypatch.getArmorNegation(hand);
		
		this.animation = animation;
	}
	
	public EpicFightEntityDamageSource(String msgId, Entity owner, StunType stunType, float impact, float armorNegation) {
		super(msgId, owner);
		this.damageSourceElements = new DamageSourceElements();
		this.damageSourceElements.stunType = stunType;
		this.damageSourceElements.impact = impact;
		this.damageSourceElements.armorNegation = armorNegation;
		this.animation = Animations.DUMMY_ANIMATION;
	}
	
	@Override
	public void setInitialPosition(Vec3 initialPosition) {
		this.initialPosition = initialPosition;
	}
	
	@Override
	public boolean isBasicAttack() {
		if (this.animation instanceof AttackAnimation) {
			return ((AttackAnimation)this.animation).isBasicAttackAnimation();
		}
		
		return false;
	}
	
	@Override
	public int getAnimationId() {
		return this.animation.getId();
	}
	
	@Override
	public Vec3 getSourcePosition() {
		return this.initialPosition != null ? this.initialPosition : super.getSourcePosition();
	}
	
	@Override
	public DamageSourceElements getDamageSourceElements() {
		return this.damageSourceElements;
	}
}