package ashjack.simukraftreloaded.core.registry;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.client.Gui.other.GuiRunMod;
import ashjack.simukraftreloaded.common.CourierTask;
import ashjack.simukraftreloaded.common.GameStates;
import ashjack.simukraftreloaded.common.PricesForBlocks;
import ashjack.simukraftreloaded.common.Relationship;
import ashjack.simukraftreloaded.common.jobs.JobSoldier;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.ClientProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.Commodity;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class SimukraftReloaded 
{
	public static Logger log = ClientProxy.log;
	
	/** used to detect when we are in-world (not main menu) and when player changes worlds/maps */
    public static String currentSavePath = "";
    
    /** all the folk's data (used to construct and maintain an EntityFolk) */
	public static ArrayList<FolkData> theFolks = new ArrayList<FolkData>();
	 
    /** all the building objects */
    public static ArrayList<Building> theBuildings = new ArrayList<Building>();

    /** all courier tasks */
    public static ArrayList<CourierTask> theCourierTasks = new ArrayList<CourierTask>();

    /** all courier points */
    public static ArrayList<V3> theCourierPoints = new ArrayList<V3>();

    /** all the mining Boxes */
    public static ArrayList<MiningBox> theMiningBoxes = new ArrayList<MiningBox>();

    /** all the farming Boxes */
    public static ArrayList<FarmingBox> theFarmingBoxes = new ArrayList<FarmingBox>();

    /** all the relationships */
    public static ArrayList<Relationship> theRelationships = new ArrayList<Relationship>();

    /** all the path constructor boxes  */
    //  public static ArrayList<PathBox> thePathBoxes=new ArrayList<PathBox>();

    /** contains all the game states and settings for this level they are playing */
    public static GameStates states = new GameStates();

    /** arraylist of commodities that banks are current selling, updates each morning with new items */
    public static ArrayList<Commodity> theCommodities = new ArrayList<Commodity>();

    /** used to upgrade crop farms in the update() call */
    public static FarmingBox farmToUpgrade = null;
    public static int farmToUpgradeCounter = 0;
    private static ArrayList<V3> farmToUpgradePoints = null;
    static public boolean isDay = true;

    public static ArrayList<V3> demolishBlocks = new ArrayList<V3>();
    public static World demolishWorld = null;
    
    public static void resetAndLoadNewWorld() {
    	Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	log.info("RESETTING and loading world on "+side.toString()+" SIDE"); 
    	theBuildings.clear();
        theCourierPoints.clear();
        theCourierTasks.clear();
        theMiningBoxes.clear();
        theFarmingBoxes.clear();
        theFolks.clear();
        theRelationships.clear();
        
        File f=new File(getSavesDataFolder() + "settings.sk2");
        if (!f.exists()) {
        	f=new File(getSavesDataFolder() + "settings.suk");
        }
        if (f.exists())
        {
            states.loadStates();

            try
            {
                GameMode.setGameModeFromNumber(states.gameModeNumber);
            }
            catch (Exception e)
            {
            	GameMode.setGameModeFromNumber(0);
            }
        }
        else
        {
            states = new GameStates();
            states.saveStates();
        }

        if (states == null)
        {
            new File(getSavesDataFolder() + "settings.sk2").delete();
            states = new GameStates();
            states.saveStates();
            sendChat("Your Sim-U-Kraft settings file was corrupted, I had to make a new one");
        }

        if (states.gameModeNumber == -1)
        {
            if (runModui == null)
            {
                GuiRunMod runModui = new GuiRunMod();
                Minecraft.getMinecraft().displayGuiScreen(runModui);
            }

            return;
        }

        if (states.gameModeNumber >= 0)
        {
        	System.out.println("Startup already been run");
            ModSimukraft.proxy.ranStartup = true;
        }

        sendChat("Welcome to Sim-U-Kraft Reloaded " + ModSimukraft.version);
        theFolks.clear();
        Building.initialiseAllBuildings(); //load ALL buildings off disk and init them
        Building.loadAllBuildings();	   //load buildings in this world
        CourierTask.loadCourierTasksAndPoints();
        MiningBox.loadMiningBoxes();
        FarmingBox.loadFarmingBoxes();
        //PathBox.loadPathBoxes();
        //FolkData.generateNewFolk(MinecraftServer.getServer().getEntityWorld());
        FolkData.loadAndSpawnFolks();
        Relationship.loadRelationships();
        updateCheck();
        isDay = isDayTime();
        Building.checkTennants();
        ModSimukraft.proxy.ranStartup = true;
    }
    
    private static GuiRunMod runModui = null;
    
    
    
    /** helper function to send chat to all players in all worlds/dimensions */
    public static void sendChat(String theText)
    {
        for (World w : MinecraftServer.getServer().worldServers)
        {
            if (!w.isRemote)
            {
                for (int i = 0; i < w.playerEntities.size(); i++)
                {
                    EntityPlayer p = (EntityPlayer) w.playerEntities.get(i);
                    //p.(ChatMessageComponent.createFromText(theText));
                    p.addChatComponentMessage(new ChatComponentText(theText));
                }
            }
        }
    }
    
    /** gets the .minecraft/saves/CURRENTWORLD/simukraft/   folder as a string  */
    public static String getSavesDataFolder()
    {
        String worldname = MinecraftServer.getServer().getFolderName();
        String strmc = new File(".").getAbsolutePath();
        strmc = strmc.substring(0, strmc.length() - 1);
        File test = new File(strmc + "saves"); //       .bodge()     :-)
        String ret = "";

        if (test.exists())   /// CLIENT SIDE
        {
            ret = new File(strmc + File.separator + "saves"
                           + File.separator + worldname + File.separator + "simukraft" + File.separator).getAbsolutePath()
            + File.separator;
        }
        else     // SERVER SIDE
        {
            strmc = strmc + worldname + File.separator + "simukraft" + File.separator;
            ret = new File(strmc).getAbsolutePath();
        }

        File f = new File(ret);

        if (!f.exists())
        {
            f.mkdirs();
        }

        return ret;
    }

    /** get the .minecraft/mods/Simukraft/  folder (contains building text files) */
    public static String getSimukraftFolder()
    {
        try
        {
            String strmc = new File(".").getAbsolutePath();
            strmc = strmc.substring(0, strmc.length() - 1);
            return new File(strmc +
                            File.separator + "mods" + File.separator + "Simukraft").getAbsolutePath();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /** returns true when it is daytime in the OVERWORLD, ignores other world times */
    public static boolean isDayTime()
    {
        if (MinecraftServer.getServer().worldServers[0].getWorldInfo().getWorldTime() % 24000 <= 11999)
        {
            return true;
        }
        else
        {
            return false;
        }

        //return MinecraftServer.getServer().worldServers[0].isDaytime();  <- doesn't work during storm
    }

    /** returns a nicely formatted money value */
    public static String displayMoney(float moneyin)
    {
        DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
        String output = myFormatter.format(moneyin);
        return output;
    }
    
    public static void updateCheck()
    {
		//REMOVED
    }

  
        public void ThreadUpdate()
        {
            start();
        }

        public void start()
        {//START HERE
        	
            try
            {
                Thread.sleep(15000);
                File check = new File(getSimukraftFolder() + "/buildings/");

                if (!check.exists())
                {
                    sendChat(getSimukraftFolder() + "/buildings/  folder is missing, Sim-U-Kraft is not correctly installed, please copy the simukraft folder AND the zip file.");
                    return;
                }

                String baseURL = "https://dl.dropboxusercontent.com/u/255688822/Sim-U-Kraft%20Reloaded/versions.txt";


                //// check for new version
                String ver = downloadFile(baseURL, getSimukraftFolder()
                                          + File.separator + "simukraft.txt");

                if (ver != null)
                {
                    ver = ver.trim();

                    if (!ver.contentEquals(""))
                    {
                        if (!ModSimukraft.version.contentEquals(ver))
                        {
                        }

                        Long now = System.currentTimeMillis();
                        states.lastUpdateCheck = now;
                        states.saveStates();
                    }
                }

                //////get new buildings
                int high = getHighestPKID("residential");
                int o = getHighestPKID("other");

                if (o > high)
                {
                    high = o;
                }

                
                
                String newbs = downloadFile(baseURL + "backend.php?cmd=getnew&n=" + high + "&i=" + getTheirId() + "&v=" + ModSimukraft.version, getSimukraftFolder()
                                            + File.separator + "simukraft.txt");

                if (newbs.length() == 0)
                {
                    return;   //No new buildings or server is broken
                }

                String[] items = newbs.split("!END"); //['pk'].['title'].['author'].['type']

                for (int i = 0; i < items.length - 1; i++)
                {
                    String[] fields = items[i].split("!F");
                    String url = baseURL + "catalogue/PKID" + fields[0] + "-" + fields[1] + ".txt";
                    String local = getSimukraftFolder() + "/buildings/" + fields[3] +
                                   "/PKID" + fields[0] + "-" + fields[1] + ".txt";
                    String ret = downloadFile(url, local);

                    if (!ret.contentEquals(""))
                    {
                        url = baseURL + "backend.php?cmd=got&pk=" + fields[0];
                        downloadFile(url, getSimukraftFolder() + File.separator + "cache.txt");
                        sendChat("Sim-U-Kraft: Downloaded new building - '" + fields[1] + "' by " + fields[2] +
                                              " (" + fields[3] + ")");
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }//END HERE
   
        private String getTheirId() {
			return null;
		}
		/*private int getHighestPKID(String string) {
			return 0;
		}*/


		int highest = 0;
        int m1 = 0;
        
        public int getHighestPKID(String type) {
		File actual = new File(getSimukraftFolder() + File.separator + "buildings" + File.separator + type + File.separator);

        for (File f : actual.listFiles())
        {
            if (f.getName().startsWith("PKID"))
            {
                m1 = f.getName().indexOf("-");

                if (m1 > 0)
                {
                    String id = f.getName().substring(4, m1);

                    if (Integer.parseInt(id) > highest)
                    {
                        highest = Integer.parseInt(id);
                    }
                }
            }
        }

        return highest;
    }

        public String downloadFile(String url, String localFile) {
        String ret = "";
		log.info("Downloading file " + url);
        url = url.replace(" ", "%20");
        
        try
        {
            java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL(url).openStream());
			java.io.FileOutputStream fos = new java.io.FileOutputStream(localFile);
            java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[4096];
            int x = 0;

            while ((x = in.read(data, 0, 4096)) >= 0)
            {
                bout.write(data, 0, x);
            }

            bout.flush();
            ret = new String(data);
            bout.close();
            in.close();
        }
        catch (Exception e)
        {
            ret = "";
            e.printStackTrace();
        }

        return ret;
    }

    public static void dayTransitionHandler()
    {
        if (isDayTime() && isDay == false)   /// Day transisition
        {
            isDay = true;
            log.info("Night to day transition");
            World world = ModSimukraft.proxy.getClientWorld();

            if (world != null)
            {
                EntityPlayer p = Minecraft.getMinecraft().thePlayer;

                if (p != null)
                {
                    ModSimukraft.proxy.getClientWorld().playSound(p.posX, p.posY, p.posZ, "ashjacksimukraftreloaded:rooster", 1.0f, 1.0f, false);
                }
            }

            states.dayOfWeek++;

            if (states.dayOfWeek > 6)
            {
                states.dayOfWeek = 0;
                int homeless=0;
                for(FolkData f:theFolks) {
                	if (f.getHome()==null) {
                		homeless++;
                	}
                }
                if (homeless>1) {
                	sendChat("There is a demand for more residential housing, you have "+homeless+" folks without a home.");
                }
                
            }

            evolveFolks();

            if (theFolks.size() > 1)
            {
                Random rand = new Random();
                int f1 = rand.nextInt(theFolks.size());
                int f2 = f1;

                while (f2 == f1)
                {
                    f2 = rand.nextInt(theFolks.size());
                }

                FolkData folk1 = theFolks.get(f1);
                FolkData folk2 = theFolks.get(f2);
                Relationship.meddleWithRelationship(folk1, folk2);
            }
        }

        if (!isDayTime() && isDay == true)
        {
            isDay = false;
            log.info("Day to Night transition");

            if (theFolks.size() > 1)
            {
                Random rand = new Random();
                int f1 = rand.nextInt(theFolks.size());
                int f2 = f1;

                while (f2 == f1)
                {
                    f2 = rand.nextInt(theFolks.size());
                }

                FolkData folk1 = theFolks.get(f1);
                FolkData folk2 = theFolks.get(f2);
                Relationship.meddleWithRelationship(folk1, folk2);
            }
            
            for(FolkData folk:theFolks) {
            	folk.destination=null;
            	if (folk.theEntity !=null) {
            		folk.theEntity.getNavigator().clearPathEntity();
            	}
            }
            
        }
    }

    //age them etc
    private static void evolveFolks()
    {
        if (theFolks.size() <= 0)
        {
            return;
        }

        FolkData folk;
        Random rand = new Random();
        log.info("evolving folks");
        //collect rent
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {

                try
                {
                    Thread.sleep(3000);
                }
                catch (Exception e) {}

                float totalRent = 0f;
                float totalCorpTax = 0f;

                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    for (int b = 0; b < theBuildings.size(); b++)
                    {
                        Building building = (Building) theBuildings.get(b);

                        if (building.type.contentEquals("residential") && building.tennants.size() > 0)
                        {
                            if (building.rent == null || building.rent == 0f)
                            {
                                building.rent = 1f;
                            }

                            log.info("Building rent for " + building.displayNameWithoutPK + ": " + building.rent + "(" +
                                building.blocksInBuilding + ")");
                            totalRent += building.rent;
                        }
                        
                        /*if (building.type.contentEquals("commercial") && FolkData.getFolkByEmployedAt(building.primaryXYZ) != null)
                        {
                            if (building.corpTax == null || building.corpTax == 0f)
                            {
                                building.corpTax = 1f;
                            }

                            log.info("Building corpTax for " + building.displayNameWithoutPK + ": " + building.corpTax + "(" +
                                building.blocksInBuilding + ")");
                            totalRent += building.corpTax;
                        }*/
                    }
                }

                if (totalRent > 0f)
                {
                    sendChat("Collected " + displayMoney(totalRent) + " Sim-u-credits in rent today.");
                    sendChat("Collected " + displayMoney(totalCorpTax) + " Sim-u-credits in corporation tax today.");
                    states.credits += totalRent;
                    states.credits += totalCorpTax;
                    EntityPlayer p = Minecraft.getMinecraft().thePlayer;

                    if (p != null)
                    {
                        ModSimukraft.proxy.getClientWorld().playSound(p.posX, p.posY, p.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
                    }
                }
                else if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    sendChat("No rent collected today, you should hire a folk to build a residential house.");
                }
            }
        });
        t.start();

        for (int f = 0; f < theFolks.size(); f++)
        {
            folk = (FolkData) theFolks.get(f);
            //reset their greet status
            folk.greetedToday = false;
            folk.matingStage = -1.0f; //not had today

            if (folk.pregnancyStage > 0.0f) //increase pregnancy - birth is in FolkData
            {
                folk.pregnancyStage += 0.1f;
            }
            
            //Age them
            int currentAge = folk.age;
            if (currentAge >= 18)
            {
                if (states.dayOfWeek == 6)  //on saturday morning
                {
                    folk.age++;
                }
            }
            else
            {
                if (states.dayOfWeek == 3 || states.dayOfWeek == 6) //age kids twice a week
                {
                    folk.age++;

                    if (currentAge == 17 && folk.age == 18) //now an adult (skin changes automatically)
                    {
                        folk.evictThem();// out of parents house
                        sendChat(folk.name+" is now 18 years old, they'll start looking for a house and you can now employ them too.");
                    }
                }
            }
            
            ///kill them when they are over 110 (1 in 10 random)
            if (folk.age > 110 && rand.nextInt(10) == 5)
            {
                sendChat(folk.name + " is old and not feeling very well...oh no!");
                folk.eventDied(DamageSource.generic);
            }
        }

        if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
        {
            ////age them one year per game week
            int fl = rand.nextInt(theFolks.size());

            for (int f = 0; f < theFolks.size(); f++)
            {
                folk = (FolkData) theFolks.get(f);

                if (f == fl)
                {
                    folk.levelFood--;

                    if (folk.levelFood == 0)
                    {
                        sendChat(folk.name + " is VERY hungry, you should build a farm, grocery, bakery or throw some food at them.");
                    }
                }
            }

            /// pay the soldiers
            for (int f = 0; f < theFolks.size(); f++)
            {
                folk = (FolkData) theFolks.get(f);

                if (folk.theirJob != null)
                {
                    if (folk.vocation == Vocation.SOLDIER)
                    {
                        JobSoldier job = (JobSoldier)folk.theirJob;
                        float pay = job.kills * 0.20f;

                        if (job.kills > 0)
                        {
                            sendChat("Paid " + folk.name + " " + displayMoney(pay) +
                                                  " Sim-u-credits for killing " + job.kills + " hostile mobs yesterday.");
                            states.credits -= pay;
                            job.kills = 0;
                        }
                    }
                }
            }

            // fluctuate block prices (Builders merchant)
            boolean updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.planks, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.cobblestone, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.stone, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.glass, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.wool, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.brick_block, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.stonebrick, updown);
            updown = rand.nextBoolean();
            PricesForBlocks.adjustPrice(Blocks.fence, updown);
        } //end of non-creative mode stuff

        states.saveStates();
        Commodity.refreshAvailableCommoditities();
    }

    public static void demolishBlocks()
    {
        if (demolishBlocks.size() < 1)
        {
            return;
        }

        int count = demolishBlocks.size();

        if (count > 10)
        {
            count = 10;
        }

        for (int i = 0; i < count; i++)
        {
            V3 blockLoc = demolishBlocks.get(0);
            Block block;

            try
            {
            	block = Block.getBlockFromName(blockLoc.name);
            	block.dropBlockAsItem(demolishWorld, blockLoc.x.intValue(), blockLoc.y.intValue() + 10 + new Random().nextInt(20), blockLoc.z.intValue()
                                      , 0, 0);
                demolishBlocks.remove(0);
            }
            catch (Exception e) {}

            
        }
    }

    /** called from commonTickHandler() every tick to upgrade a farm from one level to the next */
    public static void upgradeFarm()
    {

        if (farmToUpgrade.level == 0)
        {
            farmToUpgrade.level = 1;
        }

        if (farmToUpgrade.level == 1) //upgrade to level 2 (fence and lights)
        {
            if (farmToUpgradePoints == null)
            {
                farmToUpgradePoints = farmToUpgrade.getPerimeterPoints();
            }

            V3 point = farmToUpgradePoints.get(farmToUpgradeCounter);
            World theWorld = MinecraftServer.getServer().worldServerForDimension(point.theDimension);
            /////place fencing if the area is clear
            Block id = theWorld.getBlock(point.x.intValue(), point.y.intValue(), point.z.intValue());
            boolean destroy=false;
            if (id != null) {
            	TileEntity te=theWorld.getTileEntity(point.x.intValue(), point.y.intValue(), point.z.intValue());
            	if (te ==null) {
            		destroy=true;
            	} else {
            		if (!(te instanceof IInventory)) {
            			destroy=true;
            		}
            	}
            } else { destroy=true; }
            
        	if (destroy){
        		theWorld.func_147480_a(point.x.intValue(), point.y.intValue(), point.z.intValue(),true);
        		theWorld.setBlock(point.x.intValue(), point.y.intValue(), point.z.intValue(),
                        Blocks.fence, 0, 0x03);
        		theWorld.markBlockForUpdate(point.x.intValue(), point.y.intValue(), point.z.intValue());
        	}
            
            

            //// place lights every 6 blocks
            if (farmToUpgradeCounter % 6 == 0)
            {
                theWorld.setBlock(point.x.intValue(), point.y.intValue() - 1, point.z.intValue(),
                                  SimukraftReloadedBlocks.lightBox, 0, 0x03);
                theWorld.markBlockForUpdate(point.x.intValue(), point.y.intValue() - 1, point.z.intValue());
            }
        }
        else if (farmToUpgrade.level == 2)   //upgrade to level 3 (irrigation)
        {
            if (farmToUpgradePoints == null)
            {
                farmToUpgradePoints = farmToUpgrade.getSoilBlockPoints();
            }

            V3 point = farmToUpgradePoints.get(farmToUpgradeCounter);
            World theWorld = MinecraftServer.getServer().worldServerForDimension(point.theDimension);

            if (point.x.intValue() % 5 == 0 && point.z.intValue() % 5 == 0)
            {
                theWorld.setBlock(point.x.intValue(), point.y.intValue() - 1, point.z.intValue(), Blocks.water, 0, 0x03);
                theWorld.setBlock(point.x.intValue(), point.y.intValue() - 2, point.z.intValue(), SimukraftReloadedBlocks.lightBox, 0, 0x03);
                theWorld.markBlockForUpdate(point.x.intValue(), point.y.intValue() - 1, point.z.intValue());
                theWorld.markBlockForUpdate(point.x.intValue(), point.y.intValue() - 2, point.z.intValue());
            }
        }

        farmToUpgradeCounter++;

        if (farmToUpgradeCounter > farmToUpgradePoints.size() - 1)
        {
            farmToUpgrade.level++;
            farmToUpgradePoints = null;
            farmToUpgrade = null;
            farmToUpgradeCounter = 0;
            log.info("Finished farm upgrade");
            return;
        }
    }

    protected final static String[] dow = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    public static String getDayOfWeek()
    {
        return dow[states.dayOfWeek];
    }
    
    public static ArrayList<String> loadSK2(String fullFilename) {
    	ArrayList<String> ret=new ArrayList<String>();
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(fullFilename));

            String line = br.readLine();

            while (line != null) {
            	ret.add(line);
                line = br.readLine();
            }
            br.close();
            
    	}catch(Exception e) {e.printStackTrace();}
    	
        return ret;
    }
    
    public static void saveSK2(String fullFilename, ArrayList<String> strings) {
    	try {
    	BufferedWriter bw=new BufferedWriter(new FileWriter(fullFilename));
    	for(String line:strings) {
    		bw.write(line+"\r\n");
    	}
    	bw.close();
    	
    	}catch(Exception e) {e.printStackTrace(); }
    }
}
