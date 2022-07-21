package com.github.gamepiaynmo.custommodel.server;

import com.github.gamepiaynmo.custommodel.api.IModelSelector;
import com.github.gamepiaynmo.custommodel.server.CustomModel;
import com.github.gamepiaynmo.custommodel.server.ModConfig;
import com.github.gamepiaynmo.custommodel.server.ModelInfo;
import com.github.gamepiaynmo.custommodel.server.ModelLoadInfo;
import com.github.gamepiaynmo.custommodel.server.selector.DefaultModelSelector;
import com.github.gamepiaynmo.custommodel.util.LoadModelException;
import com.github.gamepiaynmo.custommodel.util.ModelNotFoundException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ServerModelManager {
    private final Map<UUID, ModelInfo> modelMap = Maps.newHashMap();
    public final Map<String, ModelLoadInfo> models = Maps.newHashMap();

    private final IModelSelector defaultSelector = new DefaultModelSelector();
    private IModelSelector modelSelector = defaultSelector;

    public void setModelSelector(IModelSelector modelSelector) {
        this.modelSelector = modelSelector;
        if (modelSelector == null)
            this.modelSelector = defaultSelector;
    }

    public IModelSelector getModelSelector() {
        return modelSelector;
    }

    public ModelInfo getModelForPlayer(EntityPlayer playerEntity) {
        return modelMap.get(playerEntity.getUniqueID());
    }

    public ModelInfo getModelForEntity(UUID uuid) {
        return modelMap.get(uuid);
    }

    public void refreshModelList() {
        models.clear();
        for (File file : new File(CustomModel.MODEL_DIR).listFiles()) {
            try {
                ModelInfo info = ModelInfo.fromFile(file);
                ModelLoadInfo old = models.get(info.modelId);
                if (old == null || info.version.compareTo(old.info.version) > 0)
                    models.put(info.modelId, new ModelLoadInfo(info, false));
            } catch (Exception e) {
                CustomModel.LOGGER.warn(e.getMessage(), e);
            }
        }

        for (ModelInfo info : modelMap.values()) {
            models.putIfAbsent(info.modelId, new ModelLoadInfo(info, true));
            models.get(info.modelId).refCnt++;
        }
    }

    public void addModelInfo(ModelInfo info, EntityPlayerMP sender, UUID receiverUuid) {
        ModelLoadInfo old = models.get(info.modelId);
        if ((old == null || sender.getUniqueID().equals(old.info.sender))) {
            ModelLoadInfo newInfo = new ModelLoadInfo(info, true);
            if (old != null)
                newInfo.refCnt = old.refCnt;
            models.put(info.modelId, newInfo);

            //EntityPlayerMP receiver = CustomModel.server.getPlayerList().getPlayerByUUID(receiverUuid);
            //if (receiver != null)
            //    selectModel(sender, receiver, info.modelId);
        }
    }

    private EntityLivingBase getEntityByUuid(EntityPlayerMP player, UUID uuid) {
        /*
        Entity result = CustomModel.server.getPlayerList().getPlayerByUUID(uuid);
        if (result == null && player != null)
            result = player.getServerWorld().getEntityFromUuid(uuid);
        return result instanceof EntityLivingBase ? (EntityLivingBase) result : null;

         */
        return null;
    }

    public Collection<ITextComponent> getServerModelInfoList() {
        return models.values().stream().filter(info -> !info.isClient).map(info -> info.text).collect(Collectors.toList());
    }

    public Collection<String> getServerModelIdList() {
        return models.values().stream().filter(info -> !info.isClient).map(info -> info.info.modelId).collect(Collectors.toList());
    }

    public Collection<String> getModelIdList() {
        return models.values().stream().map(info -> info.info.modelId).collect(Collectors.toList());
    }

    public void clearModel(EntityPlayerMP playerEntity) {
        GameProfile profile = playerEntity.getGameProfile();
        UUID uuid = playerEntity.getUniqueID();
        ModelInfo old = modelMap.remove(uuid);
        if (old != null)
            decRefCnt(old);
        modelSelector.clearModelForPlayer(profile);
    }

    private void decRefCnt(ModelInfo modelInfo) {
        ModelLoadInfo info = models.get(modelInfo.modelId);
        info.refCnt--;
        if (info.refCnt < 0)
            CustomModel.LOGGER.warn(info.info.modelId + " reference count < 0");
        if (info.refCnt == 0 && info.isClient)
            models.remove(info.info.modelId);
    }

    private void setModel(UUID uuid, ModelLoadInfo info) {
        info.refCnt++;
        ModelInfo old = modelMap.put(uuid, info.info);
        if (old != null)
            decRefCnt(old);
    }

    public void onPlayerExit(EntityPlayerMP player) {
        ModelInfo info = modelMap.remove(player.getUniqueID());
        if (info != null)
            decRefCnt(info);
    }

    public void reloadModel(EntityPlayerMP receiver, boolean broadcast) throws LoadModelException {
        reloadModel(receiver, receiver.getUniqueID(), broadcast);
    }

    public void reloadModel(EntityPlayerMP receiver, UUID uuid, boolean broadcast) throws LoadModelException {
        Entity entity = getEntityByUuid(receiver, uuid);
        if (entity instanceof EntityLivingBase) {
            String entry = null;
            EntityPlayerMP playerEntity = null;
            GameProfile profile = null;

            if (entity instanceof EntityPlayerMP) {
                playerEntity = (EntityPlayerMP) entity;
                profile = playerEntity.getGameProfile();
                uuid = playerEntity.getUniqueID();
                entry = modelSelector.getModelForPlayer(profile);
            }

            if (entry == null) return;
            ModelLoadInfo info = models.get(entry);
            if (info == null || receiver.getUniqueID().equals(info.info.sender)) {
                throw new ModelNotFoundException(entry);
            }
        }
    }

    public void selectModel(EntityLivingBase entity, String model) throws LoadModelException {
        selectModel(null, entity, model);
    }

    public void selectModel(EntityPlayerMP sender, EntityLivingBase entity, String model) throws LoadModelException {
        UUID uuid = entity.getUniqueID();
        ModelLoadInfo info = models.get(model);
        if (info == null) {
            throw new ModelNotFoundException(model);
        }
    }

    public void tick() {
    }
}
