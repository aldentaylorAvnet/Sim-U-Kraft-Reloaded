package ashjack.simukraftreloaded.blocks;

import java.util.Random;

import ashjack.simukraftreloaded.client.Gui.GuiBankATM;
import ashjack.simukraftreloaded.client.Gui.GuiControlBox;
import ashjack.simukraftreloaded.common.CommonProxy;
import ashjack.simukraftreloaded.common.CommonProxy.V3;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.ModSimukraft.GameMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockControlBox extends Block
{
    @SideOnly(Side.CLIENT)

    public BlockControlBox()
    {
        super(Material.wood);
        setBlockName("controlBox");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

   
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
     
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
    icons = new IIcon[4];
    icons[0] = par1IconRegister.registerIcon("ashjacksimukraftreloaded:blockControlTop");
    icons[1] = par1IconRegister.registerIcon("ashjacksimukraftreloaded:blockControlSide");
    icons[2] = par1IconRegister.registerIcon("ashjacksimukraftreloaded:blockATM");
    icons[3] = par1IconRegister.registerIcon("ashjacksimukraftreloaded:blockControlTopOther");
    }
     
    /*for (int i = 0; i < icons.length; i++)
    {
    icons[i] = par1IconRegister.registerIcon(ModSimukraft.modid + ":" + (this.getUnlocalizedName().substring(5)) + i);
    }
    }*/
     
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int par1, int par2)
    {
    switch (par2)
    {
    case 0:
    return icons[0];
    case 1:
    {
    switch (par1)
    {
    case 0:
    return icons[1];
    case 1:
    return icons[2];
    default:
    return icons[3];
    }
    }
    default:
    {
    System.out.println("Invalid metadata for " + this.getUnlocalizedName());
    return icons[0];
    }
    }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer,
                                    int par6, float par7, float par8, float par9)
    {
        world.playSoundEffect(i, j, k, "ashjacksimukraftreloaded:computer", 1f, 1f);
        GuiControlBox ui = null;
        GuiBankATM ui2 = null;
        Minecraft mc = Minecraft.getMinecraft();
        mc.setIngameNotInFocus();

        if (world.getBlockMetadata(i, j, k) == 0 || world.getBlockMetadata(i, j, k) == 2)
        {
            ui = new GuiControlBox(new V3((double)i, (double)j, (double)k, entityplayer.dimension), entityplayer);
            mc.displayGuiScreen(ui);
        }
        else
        {
            if (ModSimukraft.gameMode == GameMode.CREATIVE)
            {
                mc.displayGuiScreen(null);
                ModSimukraft.sendChat("The Bank is not active when in Creative Mode (as there's no money!)");
            }
            else
            {
                ui2 = new GuiBankATM(new V3((double)i, (double)j, (double)k, entityplayer.dimension), entityplayer);
                mc.displayGuiScreen(ui2);
            }
        }

        return true;
    }

    public int quantityDropped(Random random)
    {
        return 0;   // no recipe and no drop
    }
}
