package yesman.epicfight.gameasset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillRegistryEvent;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.ExtraDamageType;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.ActiveGuardSkill;
import yesman.epicfight.skill.AirAttack;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.BerserkerSkill;
import yesman.epicfight.skill.BladeRushSkill;
import yesman.epicfight.skill.DodgeSkill;
import yesman.epicfight.skill.EnergizingGuardSkill;
import yesman.epicfight.skill.EviscerateSkill;
import yesman.epicfight.skill.FatalDrawSkill;
import yesman.epicfight.skill.GuardSkill;
import yesman.epicfight.skill.KatanaPassive;
import yesman.epicfight.skill.KnockdownWakeupSkill;
import yesman.epicfight.skill.LethalSlicingSkill;
import yesman.epicfight.skill.LiechtenauerSkill;
import yesman.epicfight.skill.PassiveSkill;
import yesman.epicfight.skill.SimpleSpecialAttackSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SpecialAttackSkill;
import yesman.epicfight.skill.StaminaPillagerSkill;
import yesman.epicfight.skill.StepSkill;
import yesman.epicfight.skill.SwordmasterSkill;
import yesman.epicfight.skill.TechnicianSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class Skills {
	private static final Map<ResourceLocation, Skill> SKILLS = Maps.newHashMap();
	private static final Map<ResourceLocation, Skill> LEARNABLE_SKILLS = Maps.newHashMap();
	private static final Random RANDOM = new Random();
	private static int LAST_PICK = 0;
	
	static {
		SKILLS.put(new ResourceLocation(EpicFightMod.MODID, "empty"), null);
	}
	
	public static Skill getSkill(String name) {
		ResourceLocation rl;
		
		if (name.indexOf(':') >= 0) {
			rl = new ResourceLocation(name);
		} else {
			rl = new ResourceLocation(EpicFightMod.MODID, name);
		}
		
		if (SKILLS.containsKey(rl)) {
			return SKILLS.get(rl);
		} else {
			return null;
		}
	}
	
	public static Collection<ResourceLocation> getLearnableSkillNames() {
		return LEARNABLE_SKILLS.keySet();
	}
	
	public static Collection<Skill> getLearnableSkills() {
		return LEARNABLE_SKILLS.values();
	}
	
	public static String getRandomLearnableSkillName() {
		List<Skill> values = new ArrayList<Skill>(LEARNABLE_SKILLS.values());
		LAST_PICK = (LAST_PICK + RANDOM.nextInt(values.size() - 1) + 1) % values.size();
		return values.get(LAST_PICK).toString();
	}
	
	/** Default skills **/
	public static Skill BASIC_ATTACK;
	public static Skill AIR_ATTACK;
	public static Skill KNOCKDOWN_WAKEUP;
	/** Dodging skills **/
	public static Skill ROLL;
	public static Skill STEP;
	/** Guard skills **/
	public static Skill GUARD;
	public static Skill ACTIVE_GUARD;
	public static Skill ENERGIZING_GUARD;
	/** Passive skills **/
	public static Skill BERSERKER;
	public static Skill STAMINA_PILLAGER;
	public static Skill SWORD_MASTER;
	public static Skill TECHNICIAN;
	/** Special attack skills**/
	public static Function<Item, Skill> GUILLOTINE_AXE;
	public static Function<Item, Skill> SWEEPING_EDGE;
	public static Function<Item, Skill> DANCING_EDGE;
	public static Function<Item, Skill> SLAUGHTER_STANCE;
	public static Function<Item, Skill> HEARTPIERCER;
	public static Function<Item, Skill> GIANT_WHIRLWIND;
	public static Function<Item, Skill> FATAL_DRAW;
	//public static Function<Item, Skill> FATAL_DRAW2;
	public static Function<Item, Skill> LETHAL_SLICING;
	public static Function<Item, Skill> RELENTLESS_COMBO;
	public static Function<Item, Skill> LIECHTENAUER;
	public static Function<Item, Skill> EVISCERATE;
	public static Function<Item, Skill> BLADE_RUSH;
	/** passive skills **/
	public static Skill KATANA_PASSIVE;
	/** etc skills **/
	public static Skill CHARGING_JUMP;
	public static Skill GROUND_SLAM;
	
	public static void registerSkills() {
		BASIC_ATTACK = registerSkill(new BasicAttack(BasicAttack.createBuilder()));
		AIR_ATTACK = registerSkill(new AirAttack(AirAttack.createBuilder()));
		ROLL = registerSkill(new DodgeSkill(DodgeSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "roll")).setConsumption(4.0F).setAnimations(Animations.BIPED_ROLL_FORWARD, Animations.BIPED_ROLL_BACKWARD)));
		STEP = registerSkill(new StepSkill(DodgeSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "step")).setConsumption(3.0F).setAnimations(Animations.BIPED_STEP_FORWARD, Animations.BIPED_STEP_BACKWARD, Animations.BIPED_STEP_LEFT, Animations.BIPED_STEP_RIGHT)));
		KNOCKDOWN_WAKEUP = registerSkill(new KnockdownWakeupSkill(DodgeSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "knockdown_wakeup")).setConsumption(6.0F).setAnimations(Animations.BIPED_KNOCKDOWN_WAKEUP_LEFT, Animations.BIPED_KNOCKDOWN_WAKEUP_RIGHT).setCategory(SkillCategories.KNOCKDOWN_WAKEUP)));
		
		GUARD = registerSkill(new GuardSkill(GuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "guard")).setRequiredXp(5)));
		ACTIVE_GUARD = registerSkill(new ActiveGuardSkill(ActiveGuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "active_guard")).setRequiredXp(8)));
		ENERGIZING_GUARD = registerSkill(new EnergizingGuardSkill(EnergizingGuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "energizing_guard")).setRequiredXp(8)));
		
		BERSERKER = registerSkill(new BerserkerSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "berserker"))));
		STAMINA_PILLAGER = registerSkill(new StaminaPillagerSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "stamina_pillager"))));
		SWORD_MASTER = registerSkill(new SwordmasterSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "swordmaster"))));
		TECHNICIAN = registerSkill(new TechnicianSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "technician"))));
		
		SWEEPING_EDGE =(item) -> registerSpecialSkill(getSkillRegName(item, "sweeping_edge"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(30.0F).setAnimations(Animations.SWEEPING_EDGE))
				.setWeaponCategory(CapabilityItem.WeaponCategories.SWORD)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(20.0F))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.multiplier(1.6F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation()
				.setName("sweeping_edge"));
		
		DANCING_EDGE =(item) ->  registerSpecialSkill(getSkillRegName(item, "dancing_edge"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(30.0F).setAnimations(Animations.DANCING_EDGE))
				.setWeaponCategory(CapabilityItem.WeaponCategories.SWORD)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(1))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.multiplier(1.2F))
				.registerPropertiesToAnimation()
				.setName("dancing_edge"));
		
		GUILLOTINE_AXE =(item) ->  registerSpecialSkill(getSkillRegName(item, "guillotine_axe"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(20.0F).setAnimations(Animations.GUILLOTINE_AXE))
				.setWeaponCategory(CapabilityItem.WeaponCategories.AXE)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(20.0F))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation()
				.setName("guillotine_axe"));
		
		SLAUGHTER_STANCE =(item) ->  registerSpecialSkill(getSkillRegName(item, "slaughter_stance"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(40.0F).setAnimations(Animations.SPEAR_SLASH))
				.setWeaponCategory(CapabilityItem.WeaponCategories.SPEAR)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(4))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(1.25F))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.multiplier(1.2F))
				.registerPropertiesToAnimation()
				.setName("slaughter_stance"));
		
		HEARTPIERCER =(item) ->  registerSpecialSkill(getSkillRegName(item, "heartpiercer"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(40.0F).setAnimations(Animations.SPEAR_THRUST))
				.setWeaponCategory(CapabilityItem.WeaponCategories.SPEAR)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(10.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation()
				.setName("heartpiercer"));
		
		GIANT_WHIRLWIND =(item) ->  registerSpecialSkill(getSkillRegName(item, "giant_whirlwind"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(60.0F).setAnimations(Animations.GIANT_WHIRLWIND))
				.setWeaponCategory(CapabilityItem.WeaponCategories.GREATSWORD)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.multiplier(1.4F))
				.registerPropertiesToAnimation()
				.setName("giant_whirlwind"));
		
		FATAL_DRAW =(item) ->  registerSpecialSkill(getSkillRegName(item, "fatal_draw"), key -> new FatalDrawSkill(SpecialAttackSkill.createBuilder(key).setConsumption(30.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(6))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation()
				.setName("fatal_draw"));
/*
		FATAL_DRAW2 =(item) ->  registerSpecialSkill(getSkillRegName(item, "fatal_draw2"), key -> new FatalDrawSkill(SpecialAttackSkill.createBuilder(key).setConsumption(30.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(6))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation()
				.setName("fatal_draw"));
		*/
		LETHAL_SLICING =(item) ->  registerSpecialSkill(getSkillRegName(item, "lethal_slicing"), key -> new LethalSlicingSkill(SpecialAttackSkill.createBuilder(key).setConsumption(35.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(2))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.setter(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.setter(1.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(2))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(1.7F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP)
				.registerPropertiesToAnimation()
				.setName("lethal_slicing"));
		
		RELENTLESS_COMBO =(item) ->  registerSpecialSkill(getSkillRegName(item, "relentless_combo"), key -> new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(key).setConsumption(20.0F).setAnimations(Animations.RELENTLESS_COMBO))
				.setWeaponCategory(CapabilityItem.WeaponCategories.FIST)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.registerPropertiesToAnimation()
				.setName("relentless_combo"));

		LIECHTENAUER =(item) ->  registerSpecialSkill(getSkillRegName(item, "liechtenauer"), key -> new LiechtenauerSkill(SpecialAttackSkill.createBuilder(key).setConsumption(40.0F).setMaxDuration(4).setActivateType(ActivateType.DURATION_INFINITE))
				.setName("liechtenauer"));

		EVISCERATE =(item) -> {
				return registerSpecialSkill(getSkillRegName(item, "eviscerate"), key -> {
					try {
						return new EviscerateSkill(SpecialAttackSkill.createBuilder(key).setConsumption(25.0F))
								.newPropertyLine()
								.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
								.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.setter(2.0F))
								.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
								.newPropertyLine()
								.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
								.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, ExtraDamageType.get("target_lost_health",new int[] {20, 30}))
								.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(50.0F))
								.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
								.registerPropertiesToAnimation()
								.setName("eviscerate");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
		};
		
		BLADE_RUSH =(item) ->  registerSpecialSkill(getSkillRegName(item, "blade_rush"), key -> new BladeRushSkill(SpecialAttackSkill.createBuilder(key).setConsumption(25.0F).setMaxDuration(1).setMaxStack(4).setActivateType(ActivateType.TOGGLE))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(20.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
				.registerPropertiesToAnimation()
				.setName("blade_rush"));



		KATANA_PASSIVE = registerSkill(new KatanaPassive(Skill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "katana_passive"))
				.setCategory(SkillCategories.WEAPON_PASSIVE)
				.setConsumption(5.0F)
				.setActivateType(ActivateType.ONE_SHOT)
				.setResource(Resource.COOLDOWN)
		));

		//CHARGING_JUMP = registerSkill(new ChargingJumpSkill(ChargingJumpSkill.createBuilder()));
		
		SkillRegistryEvent skillRegistryEvent = new SkillRegistryEvent(SKILLS, LEARNABLE_SKILLS);
		ModLoader.get().postEvent(skillRegistryEvent);
	}

	static ResourceLocation getSkillRegName(Item item, String name) {
		return new ResourceLocation(EpicFightMod.MODID,
				name + "__" + item.getRegistryName().getNamespace()+ "_"+ item.getRegistryName().getPath());
	}

	private static Skill registerSkill(Skill skill) {
		skill = registerIfAbsent(SKILLS, skill);

		if (skill.getCategory().learnable()) {
			registerIfAbsent(LEARNABLE_SKILLS, skill);
		}

		return skill;
	}

	private static Skill registerSpecialSkill(ResourceLocation location, Function<ResourceLocation, Skill> skillSupplier) {
		Skill skill = SKILLS.get(location);
		if(skill != null) {
			return skill;
		}
		skill = skillSupplier.apply(location);
		SKILLS.put(location, skill);

		if (skill.getCategory().learnable()) {
			LEARNABLE_SKILLS.put(location, skill);
		}

		return skill;
	}

	private static Skill registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
		Skill s = map.get(skill.getRegistryName());
		if (s != null) {
			EpicFightMod.LOGGER.info("Duplicated skill name : " + skill.getRegistryName() + ". Registration was skipped.");
			return s;
		} else {
			map.put(skill.getRegistryName(), skill);
			return skill;
		}
	}
}