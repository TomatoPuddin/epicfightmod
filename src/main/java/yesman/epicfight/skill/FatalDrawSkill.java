package yesman.epicfight.skill;

import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class FatalDrawSkill extends SeperativeMotionSkill {
	public FatalDrawSkill(Builder<? extends Skill> builder) {
		super(builder, (executer)->executer.getOriginal().isSprinting() ? 1 : 0, Animations.FATAL_DRAW, Animations.FATAL_DRAW_DASH);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		boolean isSheathed = executer.getSkill(SkillCategories.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH);
		
		if (isSheathed) {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], -0.666F);
		} else {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		}
		
		this.setConsumptionSynchronize(executer, 0);
		this.setStackSynchronize(executer, executer.getSkill(this.category).getStack() - 1);
		this.setDurationSynchronize(executer, this.maxDuration);
		executer.getSkill(this.category).activate();
	}

	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		for (StaticAnimation animation : this.attackAnimations) {
			AttackAnimation anim = ((AttackAnimation)animation);
			for (AttackAnimation.Phase phase : anim.phases) {
				phase.setWeaponCategory(CapabilityItem.WeaponCategories.KATANA);
				// phase.addProperties(this.properties.get(0).entrySet());
			}
		}
		return super.registerPropertiesToAnimation();
	}
}