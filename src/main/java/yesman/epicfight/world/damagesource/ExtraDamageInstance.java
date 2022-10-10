package yesman.epicfight.world.damagesource;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ExtraDamageInstance {
	public static final ExtraDamage TARGET_LOST_HEALTH = new ExtraDamage((attacker, target, baseDamage, params) ->
			(target.getMaxHealth() - target.getHealth()) * (float)params[0], "damage.epicfight.target_lost_health");
	
	public static final ExtraDamage SWEEPING_EDGE_ENCHANTMENT = new ExtraDamage((attacker, target, baseDamage, params) -> {
			int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, attacker);
			float modifier = (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F;
			return baseDamage * modifier;
		}, "damage.epicfight.sweeping_edge_enchant");
	
	private ExtraDamage calculator;
	private float[] params;
	
	public ExtraDamageInstance(ExtraDamage calculator, float... params) {
		this.calculator = calculator;
		this.params = params;
	}
	
	public float[] getParams() {
		return this.params;
	}
	
	public Object[] toTransableComponentParams() {
		Object[] params = new Object[this.params.length];
		
		for (int i = 0; i < params.length; i++) {
			params[i] = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.params[i] * 100F) + "%").withStyle(ChatFormatting.RED);
		}
		
		return params;
	}
	
	public float get(LivingEntity attacker, LivingEntity target, float baseDamage) {
		return this.calculator.extraDamage.getBonusDamage(attacker, target, baseDamage, this.params);
	}
	
	@Override
	public String toString() {
		return this.calculator.tooltip;
	}
	
	@FunctionalInterface
	public interface ExtraDamageFunction {
		float getBonusDamage(LivingEntity attacker, LivingEntity target, float baseDamage, float[] params);
	}
	
	public static class ExtraDamage {
		ExtraDamageFunction extraDamage;
		String tooltip;
		
		public ExtraDamage(ExtraDamageFunction extraDamage, String tooltip) {
			this.extraDamage = extraDamage;
			this.tooltip = tooltip;
		}
		
		public ExtraDamageInstance create(float... params) {
			return new ExtraDamageInstance(this, params);
		}
	}
}
