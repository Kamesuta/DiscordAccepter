package net.teamfruit.discordaccepter.discord;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AcceptData implements IData {
	public Map<String, Integer> rolePower = Maps.newHashMap();
	public Map<String, Integer> userPower = Maps.newHashMap();
	public Set<String> acceptChannel = Sets.newHashSet();

	@Override
	public int getFormat() {
		return IData.FormatVersion;
	}
}
