package ashjack.simukraftreloaded.common;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.common.CommonProxy.V3;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.core.ModSimukraft;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;

public class CourierTask implements Serializable
{
    private static final long serialVersionUID = 1825349728277158061L;

    public String name = "";
    public String folkname = "";
    public V3 pickup = new V3();
    public V3 dropoff = new V3();
    public boolean repeat = true;

    /**Returns a V3 vector of a courier point when you pass in the name of the courier point */
    public static V3 getCourierPoint(String name)
    {
        V3 v = new V3();

        for (int x = 0; x < ModSimukraft.theCourierPoints.size(); x++)
        {
            v = ModSimukraft.theCourierPoints.get(x);

            if (v.name.contentEquals(name))
            {
                return v;
            }
        }

        return null;
    }

    private static boolean alreadyGotTask(CourierTask theTask)
    {
        boolean got = false;

        for (int i = 0; i < ModSimukraft.theCourierTasks.size(); i++)
        {
            CourierTask checkTask = ModSimukraft.theCourierTasks.get(i);

            try
            {
                if (checkTask.pickup.name.contentEquals(theTask.name))
                {
                    got = true;
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return got;
    }

    private static boolean alreadyGotPoint(V3 thePoint)
    {
        boolean got = false;

        for (int i = 0; i < ModSimukraft.theCourierPoints.size(); i++)
        {
            V3 checkPoint = ModSimukraft.theCourierPoints.get(i);

            if (checkPoint.isSameCoordsAs(thePoint, true, true))
            {
                got = true;
                break;
            }
        }

        return got;
    }

    public static void loadCourierTasksAndPoints()
    {
        ModSimukraft.theCourierPoints.clear();
        ModSimukraft.theCourierTasks.clear();
        File courierPoints = new File(ModSimukraft.getSavesDataFolder() + "CourierPoints" + File.separator);
        courierPoints.mkdirs();
        File courierTasks = new File(ModSimukraft.getSavesDataFolder() + "CourierTasks" + File.separator);
        courierTasks.mkdirs();

        boolean useNewFormat=false;
        //check for new file format
        for (File f : courierPoints.listFiles())
        {
            if (f.getName().endsWith(".sk2")){
            	useNewFormat=true;
            	break;
            }
        }
        
        if (useNewFormat) {
        	for (File f : courierPoints.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=ModSimukraft.loadSK2(f.getAbsoluteFile().toString());
	            	V3 v=new V3();
	            	for(String line:strings) {
	            		if (line.contains("|")) {
	            			int m1=line.indexOf("|");
	        				String name=line.substring(0,m1);
	        				String value=line.substring(m1+1);
	        				
	        				if (name.contentEquals("location")) {
	        					v=new V3(value);
	        				} else if (name.contentEquals("name")) {
	        					v.name=value;
	        				}
	            		}
	            	}
	            	if (v !=null && !alreadyGotPoint(v))
	                {
	                    ModSimukraft.theCourierPoints.add(v);
	                }
	                else
	                {
	                    f.delete();
	                }
	            }
	        }
        	
        	for (File f : courierTasks.listFiles())
	        {
	            if (f.getName().endsWith(".sk2"))
	            {
	            	ArrayList<String> strings=ModSimukraft.loadSK2(f.getAbsoluteFile().toString());
	            	CourierTask ct=new CourierTask();
	            	for(String line:strings) {
	            		if (line.contains("|")) {
	            			int m1=line.indexOf("|");
	        				String name=line.substring(0,m1);
	        				String value=line.substring(m1+1);
	        				
	        				if (name.contentEquals("folk")) {
	        					ct.folkname=value;
	        				} else if (name.contentEquals("pickup")) {
	        					ct.pickup=new V3(value);
	        				} else if (name.contentEquals("dropoff")) {
	        					if (!value.contentEquals("null")) {
	        						ct.dropoff=new V3(value);
	        					}
	        				} else if (name.contentEquals("repeat")) {
	        					ct.repeat=Boolean.parseBoolean(value);
	        				} else if (name.contentEquals("name")) {
	        					ct.name=value;
	        				}
	            		}
	            	}
	            	if (!alreadyGotTask(ct) && ct !=null && ct.dropoff !=null && ct.pickup !=null)
	                {
	                    ModSimukraft.theCourierTasks.add(ct);
	                }
	                else
	                {
	                    f.delete();
	                }
	            }
	        }
        	
        	
        } else { //old format
	        for (File f : courierPoints.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                V3 point = (V3) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
	
	                if (!alreadyGotPoint(point))
	                {
	                    ModSimukraft.theCourierPoints.add(point);
	                }
	                else
	                {
	                    f.delete();
	                }
	            }
	        }
	
	        for (File f : courierTasks.listFiles())
	        {
	            if (f.getName().endsWith(".suk"))
	            {
	                CourierTask task = (CourierTask) ModSimukraft.proxy.loadObject(f.getAbsoluteFile().toString());
	
	                if (!alreadyGotTask(task))
	                {
	                    ModSimukraft.theCourierTasks.add(task);
	                }
	                else
	                {
	                    f.delete();
	                }
	            }
	        }
        
    }
    }

    public static void saveCourierTasksAndPoints()
    {
        String names = "";

        Side side = cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide();
    	if (side==Side.SERVER) { 
    		
    		
	        for (int mofo = 0; mofo < ModSimukraft.theCourierPoints.size(); mofo++)
	        {
	        	ArrayList<String> strings=new ArrayList<String>();
	            V3 point = (V3) ModSimukraft.theCourierPoints.get(mofo);
	
	            if (point != null)
	            {
	                ArrayList<IInventory> chests = Job.inventoriesFindClosest(point, 5);
	                String fn = "cp" + point.x.intValue() + "_"
	                            + point.y.intValue() + "_" + point.z.intValue() + "_D" + point.theDimension;
	
	                if (chests.size() > 0)
	                {
	                    if (!names.contains(point.name))
	                    {
	                        //ModSimukraft.proxy.saveObject(ModSimukraft.getSavesDataFolder() + "CourierPoints"
	                                                     // + File.separator + fn + ".suk", point);
	                        names += " " + point.name;
	                        strings.add("location|"+point.toString());
	                        strings.add("name|"+point.name);
	                        ModSimukraft.saveSK2(ModSimukraft.getSavesDataFolder() + "CourierPoints" + File.separator + fn + ".sk2", strings);
	                    }
	                }
	                else
	                {
	                    try
	                    {
	                        File fi = new File(ModSimukraft.getSavesDataFolder() + "CourierPoints"
	                                           + File.separator + fn + ".sk2");
	                        fi.delete();
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        }
	
	        for (int mofo = 0; mofo < ModSimukraft.theCourierTasks.size(); mofo++)
	        {
	        	ArrayList<String> strings=new ArrayList<String>();
	        	CourierTask task = (CourierTask) ModSimukraft.theCourierTasks.get(mofo);
	            String fn = "ct" + mofo + task.folkname.replace(" ", "");
	            boolean okToSave=true;
	            strings.add("folk|"+task.folkname);
	            strings.add("pickup|"+task.pickup.toString());
	            if (task.dropoff==null) {
	            	strings.add("dropoff|null");
	            } else {
	            	try {
	            		strings.add("dropoff|"+task.dropoff.toString());
	            	} catch(Exception e) {
	            		okToSave=false;
	            	}
	            }
	            strings.add("repeat|"+task.repeat);
	            strings.add("name|"+task.name);
	            if (okToSave) {
	            	ModSimukraft.saveSK2(ModSimukraft.getSavesDataFolder() + "CourierTasks" + File.separator
	                                          + fn + ".sk2", strings);
	            }
	        }
	    }
    }
}
