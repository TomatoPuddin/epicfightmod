package yesman.epicfight.gameasset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillRegisterEvent;
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
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.StaminaPillagerSkill;
import yesman.epicfight.skill.StepSkill;
import yesman.epicfight.skill.SwordmasterSkill;
import yesman.epicfight.skill.TechnicianSkill;
import yesman.epicfight.skill.WeaponInnateSkill;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

public class Skills {
	private static final Map<ResourceLocation, Skill> SKILLS = Maps.newHashMap();
	private static final Map<ResourceLocation, Skill> LEARNABLE_SKILLS = Maps.newHashMap();
	private static final Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> BUILDERS = Maps.newHashMap();
	private static final Random RANDOM = new Random();
	private static int LAST_PICK = 0;
	
	public static Stream<ResourceLocation> getLearnableSkillNames() {
		return BUILDERS.values().stream().map(map -> map.getFirst()).filter(builder -> builder.isLearnable()).map(builder -> builder.getRegistryName());
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
	
	public static void firstRegisterSkills() {
		SkillRegisterEvent.OnRegister onRegister = new SkillRegisterEvent.OnRegister(BUILDERS);
		
		onRegister.register(BasicAttack::new, BasicAttack.createBasicAttackBuilder(), EpicFightMod.MODID, "basic_attack");
		onRegister.register(AirAttack::new, AirAttack.createAirAttackBuilder(), EpicFightMod.MODID, "air_attack");
		onRegister.register(DodgeSkill::new, DodgeSkill.createDodgeBuilder().setConsumption(4.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/roll_forward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/roll_backward")), EpicFightMod.MODID, "roll");
		onRegister.register(StepSkill::new, DodgeSkill.createDodgeBuilder().setConsumption(3.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_forward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_backward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_left"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_right")), EpicFightMod.MODID, "step");
		onRegister.register(KnockdownWakeupSkill::new, DodgeSkill.createDodgeBuilder().setConsumption(6.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/knockdown_wakeup_left"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/knockdown_wakeup_right")).setCategory(SkillCategories.KNOCKDOWN_WAKEUP), EpicFightMod.MODID, "knockdown_wakeup");
		
		onRegister.register(GuardSkill::new, GuardSkill.createGuardBuilder().setRequiredXp(5), EpicFightMod.MODID, "guard");
		onRegister.register(ActiveGuardSkill::new, ActiveGuardSkill.createActiveGuardBuilder().setRequiredXp(8), EpicFightMod.MODID, "active_guard");
		onRegister.register(EnergizingGuardSkill::new, EnergizingGuardSkill.createEnergizingGuardBuilder().setRequiredXp(8), EpicFightMod.MODID, "energizing_guard");
		
		onRegister.register(BerserkerSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "berserker");
		onRegister.register(StaminaPillagerSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "stamina_pillager");
		onRegister.register(SwordmasterSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "swordmaster");
		onRegister.register(TechnicianSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "technician");
		
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(30.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/sweeping_edge")), EpicFightMod.MODID, "sweeping_edge");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(30.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/dancing_edge")), EpicFightMod.MODID, "dancing_edge");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(20.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/guillotine_axe")), EpicFightMod.MODID, "guillotine_axe");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(40.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/spear_slash")), EpicFightMod.MODID, "slaughter_stance");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(40.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/spear_thrust")), EpicFightMod.MODID, "heartpiercer");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(60.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/giant_whirlwind")), EpicFightMod.MODID, "giant_whirlwind");
		onRegister.register(FatalDrawSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setConsumption(30.0F), EpicFightMod.MODID, "fatal_draw");
		onRegister.register(KatanaPassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE).setConsumption(5.0F).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.COOLDOWN), EpicFightMod.MODID, "katana_passive");
		onRegister.register(LethalSlicingSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setConsumption(35.0F), EpicFightMod.MODID, "lethal_slicing");
		onRegister.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setConsumption(20.0F).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/relentless_combo")), EpicFightMod.MODID, "relentless_combo");
		onRegister.register(LiechtenauerSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setConsumption(40.0F).setMaxDuration(4).setActivateType(ActivateType.DURATION_INFINITE), EpicFightMod.MODID, "liechtenauer");
		onRegister.register(EviscerateSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setConsumption(25.0F), EpicFightMod.MODID, "eviscerate");
		onRegister.register(BladeRushSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setConsumption(25.0F).setMaxDuration(1).setMaxStack(4).setActivateType(ActivateType.TOGGLE), EpicFightMod.MODID, "blade_rush");
		
		ModLoader.get().postEvent(onRegister);
	}
	
	public static void buildSkills() {
		SkillRegisterEvent.OnBuild onBuild = new SkillRegisterEvent.OnBuild(BUILDERS, SKILLS, LEARNABLE_SKILLS);
		
		BASIC_ATTACK = onBuild.build(EpicFightMod.MODID, "basic_attack");
		AIR_ATTACK = onBuild.build(EpicFightMod.MODID, "air_attack");
		ROLL = onBuild.build(EpicFightMod.MODID, "roll");
		STEP = onBuild.build(EpicFightMod.MODID, "step");
		KNOCKDOWN_WAKEUP = onBuild.build(EpicFightMod.MODID, "knockdown_wakeup");
		
		GUARD = onBuild.build(EpicFightMod.MODID, "guard");
		ACTIVE_GUARD = onBuild.build(EpicFightMod.MODID, "active_guard");
		ENERGIZING_GUARD = onBuild.build(EpicFightMod.MODID, "energizing_guard");
		
		BERSERKER = onBuild.build(EpicFightMod.MODID, "berserker");
		STAMINA_PILLAGER = onBuild.build(EpicFightMod.MODID, "stamina_pillager");
		SWORD_MASTER = onBuild.build(EpicFightMod.MODID, "swordmaster");
		TECHNICIAN = onBuild.build(EpicFightMod.MODID, "technician");
		
		WeaponInnateSkill sweepingEdge = onBuild.build(EpicFightMod.MODID, "sweeping_edge");
		sweepingEdge.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		SWEEPING_EDGE = sweepingEdge;
		
		WeaponInnateSkill dancingEdge = onBuild.build(EpicFightMod.MODID, "dancing_edge");
		dancingEdge.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		DANCING_EDGE = dancingEdge;
		
		WeaponInnateSkill guillotineAxe = onBuild.build(EpicFightMod.MODID, "guillotine_axe");
		guillotineAxe.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		GUILLOTINE_AXE = guillotineAxe;
		
		WeaponInnateSkill slaughterStance = onBuild.build(EpicFightMod.MODID, "slaughter_stance");
		slaughterStance.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(4))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		SLAUGHTER_STANCE = slaughterStance;
		
		WeaponInnateSkill heartpiercer = onBuild.build(EpicFightMod.MODID, "heartpiercer");
		heartpiercer.newProperty()
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		HEARTPIERCER = heartpiercer;
		
		WeaponInnateSkill giantWhirlwind = onBuild.build(EpicFightMod.MODID, "giant_whirlwind");
		giantWhirlwind.newProperty()
					.newProperty()
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		GIANT_WHIRLWIND = giantWhirlwind;
		
		KATANA_PASSIVE = onBuild.build(EpicFightMod.MODID, "katana_passive");
		
		WeaponInnateSkill fatalDraw = onBuild.build(EpicFightMod.MODID, "fatal_draw");
		fatalDraw.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(6))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		FATAL_DRAW = fatalDraw;
		
		WeaponInnateSkill lethalSlicing = onBuild.build(EpicFightMod.MODID, "lethal_slicing");
		lethalSlicing.newProperty()
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
					.registerPropertiesToAnimation();
		LETHAL_SLICING = lethalSlicing;
		
		WeaponInnateSkill relentlessCombo = onBuild.build(EpicFightMod.MODID, "relentless_combo");
		relentlessCombo.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.registerPropertiesToAnimation();
		RELENTLESS_COMBO = relentlessCombo;
		
		LIECHTENAUER = onBuild.build(EpicFightMod.MODID, "liechtenauer");
		
		WeaponInnateSkill eviscerate = onBuild.build(EpicFightMod.MODID, "eviscerate");
		eviscerate.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(), ExtraDamageInstance.TARGET_LOST_HEALTH.create(0.5F)))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation();
		EVISCERATE = eviscerate;
		
		WeaponInnateSkill bladeRush = onBuild.build(EpicFightMod.MODID, "blade_rush");
		bladeRush.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER)
					.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.BLADE_RUSH_SKILL)
				.registerPropertiesToAnimation();
		BLADE_RUSH = bladeRush;
		
		ModLoader.get().postEvent(onBuild);
	}
}