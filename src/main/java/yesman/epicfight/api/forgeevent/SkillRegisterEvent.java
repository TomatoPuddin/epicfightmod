package yesman.epicfight.api.forgeevent;

import java.util.Map;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.Builder;

public class SkillRegisterEvent extends Event implements IModBusEvent {
	protected final Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> builders;
	
	public SkillRegisterEvent(Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> builders) {
		this.builders = builders;
	}
	
	public static class OnRegister extends SkillRegisterEvent {
		public OnRegister(Map<ResourceLocation, Pair<? extends Builder<?>, Function<? extends Builder<?>, ? extends Skill>>> builders) {
			super(builders);
		}
		
		public <T extends Skill, B extends Skill.Builder<T>> void register(Function<B, T> constructor, B builder, String modid, String name) {
			ResourceLocation registryName = new ResourceLocation(modid, name);
			
			this.builders.put(registryName, Pair.of(builder.setRegistryName(registryName), constructor));
		}
	}
	
	public static class OnBuild extends SkillRegisterEvent {
		Map<ResourceLocation, Skill> skills;
		Map<ResourceLocation, Skill> learnableSkills;
		
		public OnBuild(Map<ResourceLocation, Pair<? extends Builder<?>, Function<? extends Builder<?>, ? extends Skill>>> builders,
				Map<ResourceLocation, Skill> skills, Map<ResourceLocation, Skill> learnableSkills) {
			super(builders);
			
			this.skills = skills;
			this.learnableSkills = learnableSkills;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Skill, B extends Skill.Builder<T>> T build(String modid, String name) {
			try {
				ResourceLocation registryName = new ResourceLocation(modid, name);
				Pair<B, Function<B, T>> pair = (Pair<B, Function<B, T>>) (Object)this.builders.get(registryName);
				
				if (pair == null) {
					Exception e = new IllegalArgumentException("Can't find the skill " + registryName + " in the registry");
					e.printStackTrace();
				}
				
				T skill = pair.getSecond().apply(pair.getFirst());
				
				if (skill != null) {
					this.skills.put(registryName, skill);
					
					if (skill.getCategory().learnable()) {
						this.learnableSkills.put(registryName, skill);
					}
				}
				
				return skill;
				
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}