package yesman.epicfight.world.damagesource;

import net.minecraft.ChatFormatting;

public enum StunType {
	NONE(ChatFormatting.GRAY + "NONE"),
	SHORT(ChatFormatting.GREEN + "SHORT" + ChatFormatting.DARK_GRAY + " stun"),
	LONG(ChatFormatting.GOLD + "LONG" + ChatFormatting.DARK_GRAY + " stun"),
	HOLD(ChatFormatting.RED + "HOLD"),
	KNOCKDOWN(ChatFormatting.RED + "KNOCKDOWN"),
	FALL(ChatFormatting.GRAY + "FALL");
	
	private String tooltip;
	
	StunType(String tooltip) {
		this.tooltip = tooltip;
	}
	
	@Override
	public String toString() {
		return tooltip;
	}
}