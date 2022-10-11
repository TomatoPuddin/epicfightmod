package yesman.epicfight.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class EpicFightMobEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EpicFightMod.MODID);
	
	public static final RegistryObject<MobEffect> STUN_IMMUNITY = EFFECTS.register("stun_immunity", () -> new VisibleMobEffect(MobEffectCategory.BENEFICIAL, "stun_immunity", 16758016));
	public static final RegistryObject<MobEffect> BLOOMING = EFFECTS.register("blooming", () -> new VisibleMobEffect(MobEffectCategory.BENEFICIAL, "blooming", 16735744));
	
	public static void addOffhandModifier() {
		MobEffects.DAMAGE_BOOST.addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get(), "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, AttributeModifier.Operation.ADDITION);
		MobEffects.WEAKNESS.addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get(), "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION);
		MobEffects.DIG_SPEED.addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get(), "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
		MobEffects.DIG_SLOWDOWN.addAttributeModifier(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get(), "55FCED67-E92A-486E-9800-B47F202C4386", -0.1D, AttributeModifier.Operation.MULTIPLY_TOTAL);
	}
}