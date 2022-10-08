package yesman.epicfight.world.damagesource;

import java.util.Set;

import yesman.epicfight.api.utils.math.ValueModifier;

public class DamageSourceElements {
	ValueModifier damageModifier;
	float impact;
	float armorNegation;
	StunType stunType;
	Set<SourceTag> sourceTag;
}