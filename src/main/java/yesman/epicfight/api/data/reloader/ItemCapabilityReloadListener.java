package yesman.epicfight.api.data.reloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.MutablePair;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.math.ExtraDamageType;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.skill.SpecialAttackSkill;
import yesman.epicfight.world.capabilities.item.ArmorCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.TagBasedSeparativeCapability;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.WeaponCapabilityPresets;
import yesman.epicfight.world.capabilities.provider.ProviderItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ItemCapabilityReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<Item, CompoundTag> CAPABILITY_ARMOR_DATA_MAP = Maps.newHashMap();
	private static final Map<Item, CompoundTag> CAPABILITY_WEAPON_DATA_MAP = Maps.newHashMap();
	
	public ItemCapabilityReloadListener() {
		super(GSON, "capabilities");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String path = rl.getPath();
			
			if (path.contains("/")) {
				String[] str = path.split("/", 2);
				ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), str[1]);
				Item item = ForgeRegistries.ITEMS.getValue(registryName);
				
				if (item == null) {
					EpicFightMod.LOGGER.warn("Tried to add a capabiltiy for item " + registryName + ", but it's not exist!");
					return;
				}
				
				CompoundTag nbt = null;
				
				try {
					nbt = TagParser.parseTag(entry.getValue().toString());
				} catch (CommandSyntaxException e) {
					e.printStackTrace();
				}
				
				if (str[0].equals("armors")) {
					CapabilityItem capability = deserializeArmor(item, nbt);
					ProviderItem.put(item, capability);
					CAPABILITY_ARMOR_DATA_MAP.put(item, nbt);
				} else if (str[0].equals("weapons")) {
					CapabilityItem capability = null;
					try {
						capability = deserializeWeapon(item, nbt, null);
					} catch (Exception e) {
						throw new RuntimeException("fail to parse EpicFight custom item data pack:" + registryName.toString(), e);
					}
					ProviderItem.put(item, capability);
					CAPABILITY_WEAPON_DATA_MAP.put(item, nbt);
				}
			}
		}
		
		ProviderItem.addDefaultItems();
	}
	
	public static CapabilityItem deserializeArmor(Item item, CompoundTag tag) {
		ArmorCapability.Builder builder = ArmorCapability.builder();
		
		if (tag.contains("attributes")) {
			CompoundTag attributes = tag.getCompound("attributes");
			builder.weight(attributes.getDouble("weight")).stunArmor(attributes.getDouble("stun_armor"));
		}
		
		builder.item(item);
		
		return builder.build();
	}
	
	public static CapabilityItem deserializeWeapon(Item item, CompoundTag tag, CapabilityItem.Builder defaultCapability) throws Exception {
		CapabilityItem capability;
		
		if (tag.contains("variations")) {
			ListTag jsonArray = tag.getList("variations", 10);
			List<Pair<Predicate<ItemStack>, CapabilityItem>> list = Lists.newArrayList();
			CapabilityItem.Builder innerDefaultCapabilityBuilder = tag.contains("type") ? WeaponCapabilityPresets.get(tag.getString("type")).apply(item) : CapabilityItem.builder();
			
			for (Tag jsonElement : jsonArray) {
				CompoundTag innerTag = ((CompoundTag)jsonElement);
				String nbtKey = innerTag.getString("nbt_key");
				String nbtValue = innerTag.getString("nbt_value");
				Predicate<ItemStack> predicate = (itemstack) -> {
					CompoundTag compound = itemstack.getTag();
					
					if (compound == null) {
						return false;
					}
					
					return compound.contains(nbtKey) ? compound.getString(nbtKey).equals(nbtValue) : false;
				};
				
				list.add(Pair.of(predicate, deserializeWeapon(item, innerTag, innerDefaultCapabilityBuilder)));
			}
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributes(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						innerDefaultCapabilityBuilder.addStyleAttibutes(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}
			
			capability = new TagBasedSeparativeCapability(list, innerDefaultCapabilityBuilder.build());
		} else {
			CapabilityItem.Builder builder = tag.contains("type") ? WeaponCapabilityPresets.get(tag.getString("type")).apply(item) : CapabilityItem.builder();
			
			if (tag.contains("attributes")) {
				CompoundTag attributes = tag.getCompound("attributes");
				
				for (String key : attributes.getAllKeys()) {
					Map<Attribute, AttributeModifier> attributeEntry = deserializeAttributes(attributes.getCompound(key));
					
					for (Map.Entry<Attribute, AttributeModifier> attribute : attributeEntry.entrySet()) {
						builder.addStyleAttibutes(Style.ENUM_MANAGER.get(key), Pair.of(attribute.getKey(), attribute.getValue()));
					}
				}
			}

			if (tag.contains("special_skills")) {
				WeaponCapability.Builder weaponBuilder = (WeaponCapability.Builder)builder;
				CompoundTag skillTags = tag.getCompound("special_skills");

				for (String key : skillTags.getAllKeys()) {
					SpecialAttackSkill skill = weaponBuilder.getSpecialAttackSkill(Style.ENUM_MANAGER.get(key));
					CompoundTag skillTag = skillTags.getCompound(key);
					if(skillTag.contains("attributes")) {
						CompoundTag skillAttrTag = skillTag.getCompound("attributes");
						for (String skillPhaseStr : skillAttrTag.getAllKeys()) {
							CompoundTag skillPhaseAttrTag = skillAttrTag.getCompound(skillPhaseStr);
							int skillPhase = Integer.parseInt(skillPhaseStr) - 1;

							for (String attrName : skillPhaseAttrTag.getAllKeys()) {
								if(attrName.equals("max_strikes")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.MAX_STRIKES,
											readValueCorrector(skillPhaseAttrTag.getCompound(attrName)));
								} else if(attrName.equals("damage")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.DAMAGE,
											readValueCorrector(skillPhaseAttrTag.getCompound(attrName)));
								} else if(attrName.equals("extra_damage")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.EXTRA_DAMAGE,
											readExtraDamage(skillPhaseAttrTag.getCompound(attrName)));
								} else if(attrName.equals("armor_negation")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.ARMOR_NEGATION,
											readValueCorrector(skillPhaseAttrTag.getCompound(attrName)));
								} else if(attrName.equals("impact")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.IMPACT,
											readValueCorrector(skillPhaseAttrTag.getCompound(attrName)));
								} else if(attrName.equals("hit_sound")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.HIT_SOUND,
											ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(skillPhaseAttrTag.getString(attrName))));
								} else if(attrName.equals("swing_sound")) {
									skill.setPhaseProperty(skillPhase,
											AnimationProperty.AttackPhaseProperty.SWING_SOUND,
											ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(skillPhaseAttrTag.getString(attrName))));
								}
							}
						}
					}
					if(skillTag.contains("texture")) {
						skill.setTextureName(new ResourceLocation(skillTag.getString("texture")));
					}
					if(skillTag.contains("name")) {
						skill.setName(skillTag.getString("name"));
					}
				}
			}
			
			if (tag.contains("collider") && builder instanceof WeaponCapability.Builder) {
				CompoundTag colliderTag = tag.getCompound("collider");
				Collider collider = deserializeCollider(item, colliderTag);
				((WeaponCapability.Builder)builder).collider(collider);
			}
			
			capability = builder.build();
		}
		
		return capability;
	}

	private static ExtraDamageType readExtraDamage(CompoundTag nbt) throws Exception {
		Set<String> keys = nbt.getAllKeys();
		List<Map.Entry<String, int[]>> list = new ArrayList<>();
		for (String key :keys) {
			list.add(new MutablePair<>(key,  ((ListTag)nbt.get(key)).stream().mapToInt(n -> ((NumericTag)n).getAsInt()).toArray()));
		}
		return ExtraDamageType.get(list);
	}

	private static ValueCorrector readValueCorrector(CompoundTag nbt) {
		float adder = 0f;
		float multipliers = 1f;
		float setters = 0f;
		if (nbt.contains("adder"))
			adder = nbt.getFloat("adder");
		if (nbt.contains("multiplier"))
			multipliers = nbt.getFloat("multiplier");
		if (nbt.contains("setter"))
			setters = nbt.getFloat("setter");
		return new ValueCorrector(adder, multipliers, setters);
	}

	private static Map<Attribute, AttributeModifier> deserializeAttributes(CompoundTag tag) {
		Map<Attribute, AttributeModifier> modifierMap = Maps.newHashMap();
		
		if (tag.contains("armor_negation")) {
			modifierMap.put(EpicFightAttributes.ARMOR_NEGATION.get(), EpicFightAttributes.getArmorNegationModifier(tag.getDouble("armor_negation")));
		}
		if (tag.contains("impact")) {
			modifierMap.put(EpicFightAttributes.IMPACT.get(), EpicFightAttributes.getImpactModifier(tag.getDouble("impact")));
		}
		if (tag.contains("max_strikes")) {
			modifierMap.put(EpicFightAttributes.MAX_STRIKES.get(), EpicFightAttributes.getMaxStrikesModifier(tag.getInt("max_strikes")));
		}
		if (tag.contains("damage_bonus")) {
			modifierMap.put(Attributes.ATTACK_DAMAGE, EpicFightAttributes.getDamageBonusModifier(tag.getDouble("damage_bonus")));
		}
		if (tag.contains("speed_bonus")) {
			modifierMap.put(Attributes.ATTACK_SPEED, EpicFightAttributes.getSpeedBonusModifier(tag.getDouble("speed_bonus")));
		}
		
		return modifierMap;
	}
	
	private static Collider deserializeCollider(Item item, CompoundTag tag) {
		int number = tag.getInt("number");
		
		if (number < 1) {
			EpicFightMod.LOGGER.warn("Datapack deserialization error: the number of colliders must bigger than 0! " + item);
			return null;
		}
		
		ListTag sizeVector = tag.getList("size", 6);
		ListTag centerVector = tag.getList("center", 6);
		
		double sizeX = sizeVector.getDouble(0);
		double sizeY = sizeVector.getDouble(1);
		double sizeZ = sizeVector.getDouble(2);
		
		double centerX = centerVector.getDouble(0);
		double centerY = centerVector.getDouble(1);
		double centerZ = centerVector.getDouble(2);
		
		if (sizeX < 0 || sizeY < 0 || sizeZ < 0) {
			EpicFightMod.LOGGER.warn("Datapack deserialization error: the size of the collider must be non-negative! " + item);
			return null;
		}
		
		if (number == 1) {
			return new OBBCollider(sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		} else {
			return new MultiOBBCollider(number, sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		}
	}
	
	public static Stream<CompoundTag> getArmorDataStream() {
		Stream<CompoundTag> tagStream = CAPABILITY_ARMOR_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getId(entry.getKey()));
			return entry.getValue();
		});
		return tagStream;
	}
	
	public static Stream<CompoundTag> getWeaponDataStream() {
		Stream<CompoundTag> tagStream = CAPABILITY_WEAPON_DATA_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putInt("id", Item.getId(entry.getKey()));
			return entry.getValue();
		});
		return tagStream;
	}
	
	public static int armorCount() {
		return CAPABILITY_ARMOR_DATA_MAP.size();
	}
	
	public static int weaponCount() {
		return CAPABILITY_WEAPON_DATA_MAP.size();
	}
	
	private static boolean armorReceived = false;
	private static boolean weaponReceived = false;
	
	@OnlyIn(Dist.CLIENT)
	public static void reset() {
		armorReceived = false;
		weaponReceived = false;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void processServerPacket(SPDatapackSync packet) {
		switch (packet.getType()) {
		case ARMOR:
			for (CompoundTag tag : packet.getTags()) {
				Item item = Item.byId(tag.getInt("id"));
				CAPABILITY_ARMOR_DATA_MAP.put(item, tag);
			}
			armorReceived = true;
			break;
		case WEAPON:
			for (CompoundTag tag : packet.getTags()) {
				Item item = Item.byId(tag.getInt("id"));
				CAPABILITY_WEAPON_DATA_MAP.put(item, tag);
			}
			weaponReceived = true;
			break;
		case MOB:
			break;
		}
		
		if (armorReceived && weaponReceived) {
			CAPABILITY_ARMOR_DATA_MAP.forEach((item, tag) -> {
				ProviderItem.put(item, deserializeArmor(item, tag));
			});
			
			CAPABILITY_WEAPON_DATA_MAP.forEach((item, tag) -> {
				try {
					ProviderItem.put(item, deserializeWeapon(item, tag, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			
			ProviderItem.addDefaultItems();
		}
	}
	
	
}