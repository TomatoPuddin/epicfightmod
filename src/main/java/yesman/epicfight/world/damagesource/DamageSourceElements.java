package yesman.epicfight.world.damagesource;

import java.util.Set;

import yesman.epicfight.api.utils.math.ValueModifier;

public class DamageSourceElements {
	ValueModifier damageModifier = ValueModifier.empty();
	float impact = 0.5F;
	float armorNegation = 0.0F;
	StunType stunType = StunType.SHORT;
	Set<SourceTag> sourceTag;
	Set<ExtraDamageInstance> extraDamages;
}