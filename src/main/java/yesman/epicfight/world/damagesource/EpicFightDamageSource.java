package yesman.epicfight.world.damagesource;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;

public interface EpicFightDamageSource {
	public static EpicFightEntityDamageSource causePlayerDamage(Player player, StunType stunType, StaticAnimation animation, InteractionHand hand) {
        return new EpicFightEntityDamageSource("player", player, stunType, animation, hand);
    }
	
	public static EpicFightEntityDamageSource causeMobDamage(LivingEntity mob, StunType stunType, StaticAnimation animation) {
        return new EpicFightEntityDamageSource("mob", mob, stunType, animation);
    }
	
	public static EpicFightEntityDamageSource causeDamage(String msg, LivingEntity attacker, StunType stunType, StaticAnimation animation) {
        return new EpicFightEntityDamageSource(msg, attacker, stunType, animation);
    }
	
	public DamageSourceElements getDamageSourceElements();
	
	default EpicFightDamageSource setDamageModifier(ValueModifier damageModifier) {
		this.getDamageSourceElements().damageModifier = damageModifier;
		return this;
	}
	
	default ValueModifier getDamageModifier() {
		return this.getDamageSourceElements().damageModifier;
	}
	
	default EpicFightDamageSource setImpact(float f) {
		this.getDamageSourceElements().impact = f;
		return this;
	}
	
	default float getImpact() {
		return this.getDamageSourceElements().impact;
	}
	
	default EpicFightDamageSource setArmorNegation(float f) {
		this.getDamageSourceElements().armorNegation = f;
		return this;
	}
	
	default float getArmorNegation() {
		return this.getDamageSourceElements().armorNegation;
	}
	
	default EpicFightDamageSource setStunType(StunType stunType) {
		this.getDamageSourceElements().stunType = stunType;
		return this;
	}
	
	default StunType getStunType() {
		return this.getDamageSourceElements().stunType;
	}
	
	default EpicFightDamageSource addTag(SourceTag tag) {
		if (this.getDamageSourceElements().sourceTag == null) {
			this.getDamageSourceElements().sourceTag = Sets.newHashSet();
		}
		
		this.getDamageSourceElements().sourceTag.add(tag);
		
		return this;
	}
	
	default boolean hasTag(SourceTag tag) {
		Set<SourceTag> tags = this.getDamageSourceElements().sourceTag;
		
		if (tags != null) {
			return tags.contains(tag);
		}
		
		return false;
	}
	
	public void setInitialPosition(Vec3 initialPosition);
	public boolean isBasicAttack();
	public int getAnimationId();
}