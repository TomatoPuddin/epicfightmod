package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = DamageSource.class)
public class MixinDamageSource {
	
	@Overwrite
	public static DamageSource mobAttack(LivingEntity p_19371_) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)p_19371_.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		DamageSource damageSource = new EntityDamageSource("mob", p_19371_);
		
		if (entitypatch != null) {
			//damageSource = ExtendedDamageSource.expandSource(damageSource);
		}
		
		return damageSource;
	}
	
	@Overwrite
	public static DamageSource playerAttack(Player p_19345_) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)p_19345_.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		DamageSource damageSource = new EntityDamageSource("player", p_19345_);
		
		if (entitypatch != null) {
			//damageSource = ExtendedDamageSource.expandSource(damageSource);
		}
		
		return damageSource;
	}
}