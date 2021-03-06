package net.xalcon.torchmaster.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.xalcon.torchmaster.TorchMasterMod;
import net.xalcon.torchmaster.client.renderer.TorchVolumeRenderHandler;
import net.xalcon.torchmaster.common.ModBlocks;
import net.xalcon.torchmaster.common.TorchmasterConfig;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = TorchMasterMod.MODID)
public class EventHandlerClient
{
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        ClientTorchRegistries.clearAll();
        TorchVolumeRenderHandler.clearList();
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        TorchVolumeRenderHandler.clearList();
    }

    @SubscribeEvent
    public static void onGlobalTickEvent(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            World world = Minecraft.getMinecraft().world;
            if(world != null)
            {
                ClientTorchRegistry registry = ClientTorchRegistries.getRegistryForDimension(world.provider.getDimension());
                registry.onClientTick(world);

                TorchVolumeRenderHandler.onGlobalTick(world);
            }
        }
    }

    @SubscribeEvent
    public static void onPlaySoundEvent(PlaySoundEvent event)
    {
        if("ambient.cave".equals(event.getName()))
        {
            ISound sound = event.getSound();
            if(sound == null) return;

            World world = Minecraft.getMinecraft().world;
            if(world == null) return;
            ClientTorchRegistry registry = ClientTorchRegistries.getRegistryForDimension(world.provider.getDimension());

            if(registry.isPositionInRange(sound.getXPosF(), sound.getYPosF(), sound.getZPosF()))
            {
                event.setResultSound(null);
            }
        }
    }
}
