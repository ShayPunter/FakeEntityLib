package uk.lewdev.entitylib.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import uk.lewdev.entitylib.entity.protocol.FakeEquippableEntity;
import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerNamedEntitySpawn;
import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerPlayerInfo;
import uk.lewdev.entitylib.utils.AngleUtil;
import uk.lewdev.entitylib.utils.MCVersion;

/**
 * https://mineskin.org/
 * https://namemc.com/skin/4056dde9cc057115
 * 
 * @version 1.15
 * @author Lewys Davies (Lew_)
 */
public class FakePlayer extends FakeEquippableEntity {
    
    private static String DEFAULT_SKIN = "eyJ0aW1lc3RhbXAiOjE1NjIwNzc3ODMzMzksInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkzYjUwZDdlODM4M2Y2NTZhODY1YTlhZjA1ZTNmYzk3ZWM2ZWZmOGNlYzU0NmFkZmI1MGI1M2UzZGU0ZGU2MjYifX19";
    private static String DEFAULT_SKIN_SIG = "dubcivWbARSmeEN1NJg89tvVFUuiFwZ3NBmPLNlUX4qwSDg3eqHyCEcl8fRu5Cf9R4Y4YQ782K+WtUszeRkkywChtZtyJ1jF7YbD2UDOH6N/gqTBETiKgkGrXyJ6idIfQKZ1LRhCAlj44K7bpW5OTjyyZESTmWkGc4AEXrIfEtGcvRA5oKk2JencxOwtoTnRkwMOBcZ1mezdHJGv5/iLc45B0P19SOo4yxlRM1zX/88g2euQyy+QUcXa2lROhIaARjDpvwd8BePWA0xnKD+T7h/UXl5FTouDCntcI8w0lOpo7FEOwgtMvHXo788iIZ4rJ2LWbHCIki7Dboj7ILyrAOXITCiOfYnn88ZDLW0bnah06Mqk/XkmzqamkYL8KsBAQK2u6e5mgOL0kzyRj3vKsWpbzQNFYtRDbVfaEBN+OleyeQTWlSPn2Ka7g9IzkQ21lrkYc683eP4FZABBCsPeKyXpzU0A4DjuG5WHitdsMue81CfbxwSkCgUI5DU/LDgbWDl+4S+MeLhZKg/cK+AmbPhDU9/KnGoHknUts7PhZOrz0qDsgCqOsPpYtH5SXMuSA0Anu5ozikbAdMnMVS8G4scKl0WengVKBkL+fGe6m/J3JFFyraoOQ0mL4pIUZd05MxGtpaMs2OROsn/lbH6l5dhgcsJIsKwStO6RUl87TBs=";
	
    private WrappedGameProfile playerProfile;
	
	private final WrappedDataWatcherObject playerByteWatcher = new WrappedDataWatcherObject(PlayerMetaData.playerByteIndex(), Registry.get(Byte.class));
    
    private final String name;
    
    public FakePlayer(String name, World world, String texture, String signature, double x, double y, double z, float yaw, float headPitch, float headYaw) {
		super(EntityType.PLAYER, UUID.randomUUID(), world, x, y, z, yaw, headPitch, headYaw);
		
		this.name = name;
		this.playerProfile = new WrappedGameProfile(super.getUUID(), this.name);
		this.playerProfile.getProperties()
    	.put("textures", new WrappedSignedProperty("textures", 
    			texture,
    			signature));
	}
    
    public FakePlayer(String name, Location loc, float headYaw) {
		this(name, loc.getWorld(), DEFAULT_SKIN, DEFAULT_SKIN_SIG, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), headYaw);
	}
    
    public FakePlayer(String name, World world, double x, double y, double z) {
		this(name, world, DEFAULT_SKIN, DEFAULT_SKIN_SIG, x, y, z, 0, 0, 0);
	}
	
	@Override
	public void setCustomName(String name) {
		//TODO Not implemented
	}
	
	@Override
	public String getCustomName() {
		//TODO Not implemented
		return this.name;
	}
	
	@Override
	public void setCustomNameVisible(boolean visible) {
		//TODO Not implemented
		return; // Always visible
	}
	
	@Override
	public boolean isCustomNameVisible() {
		//TODO Not implemented
		return true;
	}
	
	public final void showSecondSkinLayer(boolean enabled) {
		if(enabled) {
			super.getDataWatcher().setObject(this.playerByteWatcher, Byte.MAX_VALUE);
		} else {
			super.getDataWatcher().setObject(this.playerByteWatcher, 0);
		}
		super.sendMetaUpdate();
	}
	
	@Override
	protected void sendSpawnPacket(Player player) {
		this.assertNotDead();
		
		WrapperPlayServerPlayerInfo playerInfoPacket = new WrapperPlayServerPlayerInfo();
		playerInfoPacket.setAction(PlayerInfoAction.ADD_PLAYER);
		playerInfoPacket.setData(this.getPlayerInfoData());
		 
		WrapperPlayServerNamedEntitySpawn playerSpawnPacket = new WrapperPlayServerNamedEntitySpawn();
		
	    playerSpawnPacket.setEntityID(super.getEntityId());
	    playerSpawnPacket.setPlayerUUID(super.getUUID());
		
	    playerSpawnPacket.setX(super.getX());
	    playerSpawnPacket.setY(super.getY());
	    playerSpawnPacket.setZ(super.getZ());
	    playerSpawnPacket.setYaw(AngleUtil.fromDegrees(super.getYaw()));
	    playerSpawnPacket.setPitch(AngleUtil.fromDegrees(super.getPitch()));
	    
		// Spawn
		try {
			protocol.sendServerPacket(player, playerInfoPacket.getHandle());
			protocol.sendServerPacket(player, playerSpawnPacket.getHandle());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Set head yaw to current
		this.sendHeadYawPacket(player);
		this.sendMetaUpdate();
	}
	
	private final List<PlayerInfoData> getPlayerInfoData() {
		PlayerInfoData data = new PlayerInfoData(this.playerProfile, 1, NativeGameMode.CREATIVE, WrappedChatComponent.fromText(this.name));
		
		List<PlayerInfoData> dataList = new ArrayList<PlayerInfoData>();
		dataList.add(data);
		
		return dataList;
	}
	
	private enum PlayerMetaData {
		MC1_15(16);
		
		private int playerByte;
		
		private PlayerMetaData(int playerByte) {
			this.playerByte = playerByte;
		}
		
		public static PlayerMetaData curVer() {
			switch (MCVersion.CUR_VERSION()) {
			default:
				return MC1_15;
			}
		}
		
		public static int playerByteIndex() {
			return curVer().playerByte;
		}
	}
}