package com.github.gamepiaynmo.custommodel.client;

import com.github.gamepiaynmo.custommodel.api.ModelPackInfo;
import com.github.gamepiaynmo.custommodel.client.gui.GuiModelSelection;
import com.github.gamepiaynmo.custommodel.mixin.RenderPlayerHandler;
import com.github.gamepiaynmo.custommodel.client.render.*;
import com.github.gamepiaynmo.custommodel.server.CustomModel;
import com.github.gamepiaynmo.custommodel.server.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;

import static net.optifine.reflect.Reflector.KeyConflictContext;

public class CustomModelClient {
    public static final ClientModelManager manager = new ClientModelManager();
    public static boolean isServerModded = false;

    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean isRenderingInventory;
    public static EntityParameter inventoryEntityParameter;
    public static boolean isRenderingFirstPerson;

    private static float lastPartial;
    public static float getPartial() {
        return Minecraft.getMinecraft().isGamePaused() ? lastPartial : (lastPartial = Minecraft.getMinecraft().getRenderPartialTicks());
    }

    private static void initServerStatus() {
        isServerModded = false;
    }

    public static void onInitializeClient() {
        new File(CustomModel.MODEL_DIR).mkdirs();
        initServerStatus();
    }

    public static void initPlayerRenderer() {
        for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values())
            RenderPlayerHandler.customize(renderer);
    }

    private static WorldClient lastWorld = null;

    //ClientTickEvent TickEvent.Phase.END
    public static void onWorldTick() {
        Minecraft mc = Minecraft.getMinecraft();
        WorldClient world = mc.world;
        if (world != null && !mc.isGamePaused()) {
            for (AbstractClientPlayer player : world.getPlayers(AbstractClientPlayer.class, player -> true)) {
                RenderPlayerHandler.tick(player);
            }

            manager.tick();
        }

        if (lastWorld != null && world == null) {
            initServerStatus();
            manager.clearModels();
        }

        lastWorld = world;
    }

    public static void showModelSelectionGui(List<ModelPackInfo> infoList) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiModelSelection(infoList));
    }
}
