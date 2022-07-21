package com.github.gamepiaynmo.custommodel.server;

import com.github.gamepiaynmo.custommodel.mixin.PlayerStatureHandler;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class CustomModel {
    public static final String MODID = "custommodel";
    public static final String MODEL_DIR = "custom-models";

    public static final Logger LOGGER = LogManager.getLogger();

    public static final ServerModelManager manager = new ServerModelManager();
    public static boolean hasnpc;

    public static void onInitialize() {
        hasnpc = false;

        new File(MODEL_DIR).mkdirs();
        manager.refreshModelList();
    }

    //PlayerEvent.PlayerLoggedOutEvent
    public static void onPlayerExit(EntityLivingBase entityLivingBase) {
        manager.onPlayerExit((EntityPlayerMP) entityLivingBase);
    }
}
