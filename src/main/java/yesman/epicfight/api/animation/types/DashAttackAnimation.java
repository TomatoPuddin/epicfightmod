package yesman.epicfight.api.animation.types;

import javax.annotation.Nullable;

import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.ValueModifier;

public class DashAttackAnimation extends AttackAnimation {
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.ROTATE_X, true);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.333F));
	}
	
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, boolean noDirectionAttack, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.333F));
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}