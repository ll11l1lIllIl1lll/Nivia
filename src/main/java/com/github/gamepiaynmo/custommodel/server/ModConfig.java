package com.github.gamepiaynmo.custommodel.server;

import net.minecraft.util.text.translation.I18n;

public class ModConfig {
    public static ClientConfig client = new ClientConfig();
    public static ServerConfig server = new ServerConfig();
    public static class ClientConfig {
        public boolean hideNearParticles = true;
        public boolean sendModels = true;
        public boolean hideArmors = false;
    }

    public static class ServerConfig {
        public boolean customEyeHeight = true;

        public boolean customBoundingBox = true;

        public String defaultModel = "default";

        public boolean receiveModels = true;
    }

    public static boolean isHideNearParticles() { return client.hideNearParticles; }
    public static boolean isSendModels() { return client.sendModels; }
    public static boolean isHideArmors() { return client.hideArmors; }
    public static boolean isCustomEyeHeight() { return server.customEyeHeight; }
    public static boolean isCustomBoundingBox() { return server.customBoundingBox; }
    public static String getDefaultModel() { return server.defaultModel; }
    public static boolean isReceiveModels() { return server.receiveModels; }

}
