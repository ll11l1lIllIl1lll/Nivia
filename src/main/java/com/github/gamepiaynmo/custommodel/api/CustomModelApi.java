package com.github.gamepiaynmo.custommodel.api;

import com.github.gamepiaynmo.custommodel.server.CustomModel;
import com.github.gamepiaynmo.custommodel.server.ModelInfo;
import com.github.gamepiaynmo.custommodel.server.ModelLoadInfo;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Collection;
import java.util.List;

public interface CustomModelApi {
    /**
     * Reload the model for the specified player.
     * @param player Specified player.
     */
    public static void reloadModelForPlayer(EntityPlayerMP player) {
        CustomModel.manager.reloadModel(player, true);
    }

    /**
     * Select a model for the specified player.
     * @param player Specified player.
     * @param modelId Model ID of the model pack.
     */
    public static void selectModelForPlayer(EntityPlayerMP player, String modelId) {
        CustomModel.manager.selectModel(player, modelId);
    }

    /**
     * Select a model for the specified offline player.
     * @param playerName Specified player name.
     * @param modelId Model ID of the model pack.
     */
    public static void selectModelForOfflinePlayer(String playerName, String modelId) {
        final GameProfile profile = getGameProfilefromName(playerName);
        if (profile != null)
            CustomModel.manager.getModelSelector().setModelForPlayer(profile, modelId);
    }

    /**
     * Get the current model of the specified player.
     * @param player Specified player.
     * @return Model ID of the model pack.
     */
    public static String getCurrentModelOfPlayer(EntityPlayerMP player) {
        ModelInfo info = CustomModel.manager.getModelForEntity(player.getUniqueID());
        return info != null ? info.modelId :
                CustomModel.manager.getModelSelector().getModelForPlayer(player.getGameProfile());
    }

    /**
     * Get the current model of the specified offline player.
     * @param playerName Specified player name.
     * @return Model ID of the model pack.
     */
    public static String getCurrentModelOfOfflinePlayer(String playerName) {
        final GameProfile profile = getGameProfilefromName(playerName);
        if (profile != null) {
            ModelInfo info = CustomModel.manager.getModelForEntity(EntityPlayer.getUUID(profile));
            if (info != null)
                return info.modelId;
        }
        return CustomModel.manager.getModelSelector().getModelForPlayer(profile);
    }

    /**
     * Refresh the model list from disk files.
     */
    public static void refreshModelList() {
        CustomModel.manager.refreshModelList();
    }

    /**
     * Get the mode ID of loaded model packs.
     * @return Model ID of loaded model packs.
     */
    public static Collection<String> getModelIdList() {
        return CustomModel.manager.getServerModelIdList();
    }

    /**
     * Get the basic information of a loaded model pack.
     * @param modelId Model ID of the model pack.
     * @return Basic information of the model pack.
     */
    public static ModelPackInfo getModelPackInfo(String modelId) {
        ModelLoadInfo model = CustomModel.manager.models.get(modelId);
        return model == null ? null : model.getInfo();
    }

    private static GameProfile getGameProfilefromName(final String playerName) {
        final NetHandlerPlayClient nethandlerplayclient = Minecraft.getMinecraft().player.connection;
        final List<NetworkPlayerInfo> list = GuiPlayerTabOverlay.ENTRY_ORDERING.<NetworkPlayerInfo>sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        GameProfile profile = null;
        for (NetworkPlayerInfo networkPlayerInfo : list) {
            if(networkPlayerInfo.getGameProfile().getName().equals(playerName)) {
                profile = networkPlayerInfo.getGameProfile();
            }
        }
        return profile;
    }
}
