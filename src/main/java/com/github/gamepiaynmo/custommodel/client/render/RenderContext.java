package com.github.gamepiaynmo.custommodel.client.render;

import com.github.gamepiaynmo.custommodel.server.CustomModel;
import com.github.gamepiaynmo.custommodel.util.Matrix4;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityLivingBase;

public class RenderContext {
    public EntityLivingBase currentEntity;
    public RenderParameter currentParameter;
    public ModelPlayer currentModel;
    public CustomJsonModel currentJsonModel;
    public Matrix4 currentInvTransform;
    public boolean isInvisible;
    public boolean renderEmissive;

    private boolean isPlayer;
    private boolean isNpc;

    public void setEntity(EntityLivingBase entity) {
        currentEntity = entity;
        isPlayer = entity instanceof AbstractClientPlayer;
        isNpc = CustomModel.hasnpc;
    }

    public void setPlayer(AbstractClientPlayer player) {
        currentEntity = player;
        isPlayer = true;
        isNpc = false;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public AbstractClientPlayer getPlayer() {
        return isPlayer ? (AbstractClientPlayer) currentEntity : null;
    }

    public EntityLivingBase getEntity() {
        return currentEntity;
    }
}
