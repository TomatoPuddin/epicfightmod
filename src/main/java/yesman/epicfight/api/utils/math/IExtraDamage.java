package yesman.epicfight.api.utils.math;

import net.minecraft.world.entity.LivingEntity;

@FunctionalInterface
public interface IExtraDamage {
	float getBonusDamage(LivingEntity attacker, LivingEntity target, int[] arg);
}