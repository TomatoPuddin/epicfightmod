package yesman.epicfight.skill;

public abstract class PassiveSkill extends Skill {
	public static Skill.Builder<PassiveSkill> createPassiveBuilder() {
		return (new Skill.Builder<PassiveSkill>()).setCategory(SkillCategories.PASSIVE).setConsumption(0.0F).setMaxStack(0).setResource(Resource.NONE).setRequiredXp(5);
	}
	
	public PassiveSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
}