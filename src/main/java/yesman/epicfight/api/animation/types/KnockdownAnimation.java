package yesman.epicfight.api.animation.types;

import net.minecraft.world.damagesource.EntityDamageSource;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTag;

public class KnockdownAnimation extends LongHitAnimation {
	public KnockdownAnimation(float convertTime, float delayTime, String path, Model model) {
		super(convertTime, path, model);

		this.stateSpectrumBlueprint
			.addState(EntityState.KNOCKDOWN, true)
			.addState(EntityState.INVULNERABILITY_PREDICATE, (damagesource) -> {
				if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
					if (damagesource instanceof EpicFightDamageSource) {
						return !((EpicFightDamageSource)damagesource).hasTag(SourceTag.FINISHER);
					} else {
						return true;
					}
				}
				return false;
			});
	}
}