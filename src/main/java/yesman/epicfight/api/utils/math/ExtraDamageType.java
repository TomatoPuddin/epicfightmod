package yesman.epicfight.api.utils.math;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

public class ExtraDamageType {
	static {
		calculatorMap = new HashMap<>();
		register("target_lost_health", (attacker, target, arg) ->
				Math.min((target.getMaxHealth() - target.getHealth()) * arg[0] / 100, arg[1]), 2);
		register("target_health", (attacker, target, arg) ->
				Math.min((target.getHealth()) * arg[0] / 100, arg[1]), 2);
		register("target_max_health", (attacker, target, arg) ->
				Math.min((target.getMaxHealth()) * arg[0] / 100, arg[1]), 2);

		register("attacker_lost_health", (attacker, target, arg) ->
				Math.min((attacker.getMaxHealth() - attacker.getHealth()) * arg[0] / 100, arg[1]), 2);
		register("attacker_health", (attacker, target, arg) ->
				Math.min((attacker.getHealth()) * arg[0] / 100, arg[1]), 2);
		register("attacker_max_health", (attacker, target, arg) ->
				Math.min((attacker.getMaxHealth()) * arg[0] / 100, arg[1]), 2);

		register("attacker_hunger", (attacker, target, arg) ->{
			if(!(attacker instanceof Player))
				return 0;
			Player player = (Player) attacker;
			return Math.min(player.getFoodData().getFoodLevel() * arg[0] / 100, arg[1]);
		}, 2);

		register("attacker_xp_level", (attacker, target, arg) ->{
			if(!(attacker instanceof Player))
				return 0;
			Player player = (Player) attacker;
			return Math.min(player.experienceLevel * arg[0] / 100, arg[1]);
		}, 2);
	}

	public static void register(String name, IExtraDamage extraDamage, int argSize) {
		calculatorMap.put(name, new CalculatorType(extraDamage, name, argSize));
	}

	final static Map<String, CalculatorType> calculatorMap;

	public static ExtraDamageType get(List<Map.Entry<String, int[]>> list) throws Exception {
		List<Map.Entry<CalculatorType, int[]>> calculatorList = new ArrayList<>();
		for(Map.Entry<String, int[]> entry: list) {
			CalculatorType calculator = calculatorMap.get(entry.getKey());
			if(calculator == null)
				throw new Exception("unknown epic fight extra_damage type: " + entry.getKey());
			if(calculator.getArgSize() != entry.getValue().length)
				throw new Exception("extra_damage type " + calculator.getName() + " requires " + calculator.getArgSize() + " args. Found " + entry.getValue().length);
			calculatorList.add(new ImmutablePair<>(calculator, entry.getValue()));
		}

		return new ExtraDamageType(calculatorList);
	}

	public static ExtraDamageType get(String name, int[] args) throws Exception {
		List<Map.Entry<CalculatorType, int[]>> calculatorList = new ArrayList<>();
		CalculatorType calculator = calculatorMap.get(name);
		if(calculator == null)
			throw new Exception("unknown epic fight extra_damage type: " + name);
		if(calculator.getArgSize() != args.length)
			throw new Exception("extra_damage type " + calculator.getName() + " requires " + calculator.getArgSize() + " args. Found " + args.length);
		calculatorList.add(new ImmutablePair<>(calculator, args));
		return new ExtraDamageType(calculatorList);
	}

	private List<? extends Map.Entry<CalculatorType, int[]>> calculatorList;

	private ExtraDamageType(List<? extends Map.Entry<CalculatorType, int[]>> calculatorList) {
		this.calculatorList = calculatorList;
	}

	public List<? extends Map.Entry<CalculatorType, int[]>> getCalculator() {
		return this.calculatorList;
	}

	public float get(LivingEntity attacker, LivingEntity target) {
		float damage = 0;
		for(Map.Entry<CalculatorType, int[]> entry : calculatorList) {
			damage += entry.getKey().extraDamage.getBonusDamage(attacker, target, entry.getValue());
		}
		return damage;
	}

	public void getTooltip(List<Component> list) {
		for(Map.Entry<CalculatorType, int[]> entry: this.calculatorList) {
			if(entry.getValue()[0] <= 0)
				continue;
			Object[] args = Arrays.stream(entry.getValue()).mapToObj(i -> (Object)new TextComponent(String.valueOf(i)).withStyle(ChatFormatting.RED)).toArray();
			list.add(new TranslatableComponent(entry.getKey().getTooltip(), args).withStyle(ChatFormatting.DARK_GRAY));
		}
	}

	static class CalculatorType {
		IExtraDamage extraDamage;
		String tooltip;
		String name;
		int argSize;

		public CalculatorType(IExtraDamage extraDamage, String name, int argSize) {
			this.extraDamage = extraDamage;
			this.tooltip = "skill.epicfight.extra_damage." + name;
			this.name = name;
			this.argSize = argSize;
		}

		public String getTooltip() {
			return tooltip;
		}

		public int getArgSize() {
			return argSize;
		}

		public String getName() {
			return name;
		}
	}
}
