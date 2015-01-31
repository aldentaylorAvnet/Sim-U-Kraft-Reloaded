package ashjack.simukraftreloaded.client;

import java.io.File;

import ashjack.simukraftreloaded.core.ModSimukraft;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.sound.SoundLoadEvent;

//// NOTE: MC 1.7+ audio works ALOT different to this.

public class EventSounds
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onSound(SoundLoadEvent event)
    {
        try
        {
            String[] sounds = {"constructoractivated", "computer", "powerdown", "hellom", "hellof", "readym", "readyf",
                               "welcome", "construction", "cash", "beamdown", "CoughF", "CoughM", "SneezeF", "SneezeM", "LaughF", "LaughM",
                               "OuchF", "OuchM", "lightone", "fone", "ftwo", "fthree", "mone", "mtwo", "mthree", "mfour", "mfive", "msix",
                               "shears", "rooster", "daymone", "dayfone", "daymtwo", "dayftwo", "daymthree", "dayfthree",
                               "nightmone", "nightfone", "nightmtwo", "nightftwo", "nightmthree", "nightfthree", "merchm",
                               "bakerm", "bakerf", "beamm", "beamf", "beamdowntwo", "mnine", "fnine", "meight", "feight", "fseven", "mseven",
                               "mwxbad", "fwxbad", "cashshort", "blarga", "blargb", "blargc", "blargd", "blarge", "blargf", "blargg",
                               "blargh", "blargi", "blargj", "blargk", "blargl", "blargm", "blargn", "blargo", "blargp", "blargq", "blargr",
                               "blargs", "blargt", "blargu", "blargv", "blargw", "blargx", "blargy", "blargz", "birth", "pregnant",
                               "mspeaka", "mspeakb", "mspeakc", "mspeakd", "mspeake", "mspeakf", "mspeakg", "mspeakh", "mspeaki",
                               "mspeakj", "mspeakk", "mspeakl", "mspeakm", "mspeakn", "mspeako", "mspeakp", "mspeakq", "mspeakr",
                               "mspeaks", "fspeaka", "fspeakb", "fspeakc", "fspeakd", "fspeake", "fspeakf", "fspeakg", "fspeakh", "fspeaki",
                               "fspeakj", "fspeakk", "fspeakl", "fspeakm", "fspeakn", "fspeako", "fspeakp", "fspeakq", "fspeakr",
                               "fspeaks", "cspeaka", "cspeakb", "cspeakc", "helloc","windmill","burgerma","burgermb","burgermc",
                               "burgermd","burgerme","burgermf","burgerfa","burgerfb","burgerfc","burgerfd","burgerfe","burgerff",
                               "cheesemachine"
                              };

            for (int x = 0; x < sounds.length; x++)
            {
                //event.manager.soundPoolSounds.addSound("ashjacksimukraftreloaded:" + sounds[x] + ".ogg");
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to register one or more sounds.");
        }
    }
}