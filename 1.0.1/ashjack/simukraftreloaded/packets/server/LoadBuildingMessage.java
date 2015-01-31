package ashjack.simukraftreloaded.packets.server;

import ashjack.simukraftreloaded.common.CommonProxy.V3;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packets.AbstractServerMessageHandler;
import ashjack.simukraftreloaded.packets.client.UpdateFolkPositionMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class LoadBuildingMessage implements IMessage{
	
	private String GuiBuildingCon;

	 public LoadBuildingMessage() {}
	 
	 public LoadBuildingMessage(String GuiBuildingCon) 
	 {
		 this.GuiBuildingCon = GuiBuildingCon;
	 }

	 @Override
	 public void fromBytes(ByteBuf buf) 
	 {
		 GuiBuildingCon = ByteBufUtils.readUTF8String(buf);
	 }

	 @Override
	 public void toBytes(ByteBuf buf) 
	 {
		 ByteBufUtils.writeUTF8String(buf, GuiBuildingCon);
	 }
	 
	public static class Handler extends AbstractServerMessageHandler<LoadBuildingMessage> {
	 @Override
	 public IMessage handleServerMessage(EntityPlayer player, LoadBuildingMessage message, 
	 MessageContext ctx) {
	
	 Building.loadAllBuildings();
		 
	 //player.openGui(ModSimukraft.instance, message.id, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	 return null;
	 }
	 }

}
