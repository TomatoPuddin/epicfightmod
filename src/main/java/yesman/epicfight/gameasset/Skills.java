package yesman.epicfight.gameasset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillRegistryEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
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
import yesman.epicfight.skill.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.WeaponInnateSkill;
import yesman.epicfight.skill.StaminaPillagerSkill;
import yesman.epicfight.skill.StepSkill;
import yesman.epicfight.skill.SwordmasterSkill;
import yesman.epicfight.skill.TechnicianSkill;

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
	/** Weapon innate skills**/
	public static Skill GUILLOTINE_AXE;
	public static Skill SWEEPING_EDGE;
	public static Skill DANCING_EDGE;
	public static Skill SLAUGHTER_STANCE;
	public static Skill HEARTPIERCER;
	public static Skill GIANT_WHIRLWIND;
	public static Skill FATAL_DRAW;
	public static Skill KATANA_PASSIVE;
	public static Skill LETHAL_SLICING;
	public static Skill RELENTLESS_COMBO;
	public static Skill LIECHTENAUER;
	public static Skill EVISCERATE;
	public static Skill BLADE_RUSH;
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
		
		SWEEPING_EDGE = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "sweeping_edge")).setConsumption(30.0F).setAnimations(Animations.SWEEPING_EDGE))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		DANCING_EDGE = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "dancing_edge")).setConsumption(30.0F).setAnimations(Animations.DANCING_EDGE))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		GUILLOTINE_AXE = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "guillotine_axe")).setConsumption(20.0F).setAnimations(Animations.GUILLOTINE_AXE))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		SLAUGHTER_STANCE = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "slaughter_stance")).setConsumption(40.0F).setAnimations(Animations.SPEAR_SLASH))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(4))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		HEARTPIERCER = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "heartpiercer")).setConsumption(40.0F).setAnimations(Animations.SPEAR_THRUST))
				.newProperty()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		GIANT_WHIRLWIND = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "giant_whirlwind")).setConsumption(60.0F).setAnimations(Animations.GIANT_WHIRLWIND))
				.newProperty()
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		FATAL_DRAW = registerSkill(new FatalDrawSkill(WeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "fatal_draw")).setConsumption(30.0F))
				.newProperty()
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(6))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		KATANA_PASSIVE = registerSkill(new KatanaPassive(Skill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "katana_passive"))
				.setCategory(SkillCategories.WEAPON_PASSIVE)
				.setConsumption(5.0F)
				.setActivateType(ActivateType.ONE_SHOT)
				.setResource(Resource.COOLDOWN)
		));
		
		LETHAL_SLICING = registerSkill(new LethalSlicingSkill(WeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "lethal_slicing")).setConsumption(35.0F))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(2))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(1.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.newProperty()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2))
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		RELENTLESS_COMBO = registerSkill(new SimpleWeaponInnateSkill(SimpleWeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "relentless_combo")).setConsumption(20.0F).setAnimations(Animations.RELENTLESS_COMBO))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.registerPropertiesToAnimation());
		
		LIECHTENAUER = registerSkill(new LiechtenauerSkill(WeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "liechtenauer")).setConsumption(40.0F).setMaxDuration(4).setActivateType(ActivateType.DURATION_INFINITE)));
		
		EVISCERATE = registerSkill(new EviscerateSkill(WeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "eviscerate")).setConsumption(25.0F))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(), ExtraDamageInstance.TARGET_LOST_HEALTH.create(0.5F)))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation());
		
		BLADE_RUSH = registerSkill(new BladeRushSkill(WeaponInnateSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "blade_rush")).setConsumption(25.0F).setMaxDuration(1).setMaxStack(4).setActivateType(ActivateType.TOGGLE))
				.newProperty()
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.newProperty()
				.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
				.registerPropertiesToAnimation());
		
		//CHARGING_JUMP = registerSkill(new ChargingJumpSkill(ChargingJumpSkill.createBuilder()));
		
		SkillRegistryEvent skillRegistryEvent = new SkillRegistryEvent(SKILLS, LEARNABLE_SKILLS);
		ModLoader.get().postEvent(skillRegistryEvent);
	}
	
	private static Skill registerSkill(Skill skill) {
		registerIfAbsent(SKILLS, skill);
		
		if (skill.getCategory().learnable()) {
			registerIfAbsent(LEARNABLE_SKILLS, skill);
		}
		
		return skill;
	}
	
	private static void registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
		if (map.containsKey(skill.getRegistryName())) {
			EpicFightMod.LOGGER.info("Duplicated skill name : " + skill.getRegistryName() + ". Registration was skipped.");
		} else {
			map.put(skill.getRegistryName(), skill);
		}
	}
}