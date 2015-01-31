package ashjack.simukraftreloaded.packets.client;

import ashjack.simukraftreloaded.common.CommonProxy.V3;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packets.AbstractServerMessageHandler;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UpdateFolkPositionMessage implements IMessage {

	private String posString;
	String[] data;
	private static String folkName;
	private static String pos;

	 public UpdateFolkPositionMessage() {}
	 
	 public UpdateFolkPositionMessage(String posString) 
	 {
		 this.posString = posString;
		 data = posString.split(";");
		 pos = data[0];
		 folkName = data[1];
	 }

	 @Override
	 public void fromBytes(ByteBuf buf) 
	 {
		 posString = ByteBufUtils.readUTF8String(buf);
		 //data = posString.split(";");
	 }

	 @Override
	 public void toBytes(ByteBuf buf) 
	 {
		 ByteBufUtils.writeUTF8String(buf, posString);
	 }
	 
	public static class Handler extends AbstractServerMessageHandler<UpdateFolkPositionMessage> {
	@Override
	public IMessage handleServerMessage(EntityPlayer player, UpdateFolkPositionMessage message, 
	MessageContext ctx) {
	
		FolkData folk=FolkData.getFolkByName(folkName);
     	V3 newpos=new V3(pos);
     	if (folk !=null && newpos !=null) {
     		folk.serverToClientLocationUpdate(newpos);
     	}
		 
	 //player.openGui(ModSimukraft.instance, message.id, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
	 return null;
	 }
	 }
	}