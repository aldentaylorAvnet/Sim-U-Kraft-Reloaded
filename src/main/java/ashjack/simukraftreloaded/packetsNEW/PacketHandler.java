package ashjack.simukraftreloaded.packetsNEW;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.packetsNEW.toClient.UpdateFolkPositionPacket;
import ashjack.simukraftreloaded.packetsNEW.toServer.DemolishBuildingPacket;
import ashjack.simukraftreloaded.packetsNEW.toServer.GenerateFolkPacket;
import ashjack.simukraftreloaded.packetsNEW.toServer.LoadBuildingPacket;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
  public static SimpleNetworkWrapper net;
  
  public static void initPackets()
  {
    net = NetworkRegistry.INSTANCE.newSimpleChannel(ModSimukraft.modid);
    
    net.registerMessage(UpdateFolkPositionPacket.Handler.class, UpdateFolkPositionPacket.class, 0, Side.CLIENT);
	net.registerMessage(LoadBuildingPacket.Handler.class, LoadBuildingPacket.class, 1, Side.SERVER); 
	net.registerMessage(DemolishBuildingPacket.Handler.class, DemolishBuildingPacket.class, 2, Side.SERVER);
	net.registerMessage(GenerateFolkPacket.Handler.class, GenerateFolkPacket.class, 3, Side.SERVER);
	
  }
  
  private static int nextPacketId = 0;
  
  private static void registerMessage(Class packet, Class message)
  {
    net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
    net.registerMessage(packet, message, nextPacketId, Side.SERVER);
    nextPacketId++;
  }
}