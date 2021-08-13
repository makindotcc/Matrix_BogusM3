/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.text.Text;
import net.wurstclient.Category;
import net.wurstclient.WurstClient;
import net.wurstclient.events.PacketInputListener;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

public final class TimerHack extends Hack implements UpdateListener, PacketInputListener {
    private final SliderSetting speed =
            new SliderSetting("Speed", 2, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
    private final SliderSetting maxDuration =
            new SliderSetting("maxDuration", 20, 1, 60, 1, ValueDisplay.INTEGER);
    private final SliderSetting cooldown =
            new SliderSetting("cooldown", 20, 1, 60, 1, ValueDisplay.INTEGER);
    private final SliderSetting cooldownSpeed =
            new SliderSetting("cooldownSpeed", 2, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
    private final CheckboxSetting cdOnSetback = new CheckboxSetting("cooldownOnSetback", true);
    private int ticks = 0;
    private long disabledAt = System.currentTimeMillis();
    private boolean cd;

    public TimerHack() {
        super("Timer", "Changes the speed of almost everything.");
        setCategory(Category.OTHER);
        addSetting(speed);
        addSetting(maxDuration);
        addSetting(cooldown);
        addSetting(cooldownSpeed);
        addSetting(cdOnSetback);
    }

    @Override
    public String getRenderName() {
        StringBuilder nameBuilder = new StringBuilder(getName());
        nameBuilder.append(" [").append(speed.getValueString()).append("]");
        if (this.cd) {
            nameBuilder.append(" *CD: ")
                    .append(this.ticks).append(" ticks left");
        }
        return nameBuilder.toString();
    }

    public float getTimerSpeed() {
        if (this.isEnabled()) {
            if (this.cd) {
                return cooldownSpeed.getValueF();
            } else {
                return speed.getValueF();
            }
        } else {
            return 1.0f;
        }
    }

	@Override
	protected void onEnable() {
        long ticksElapsedSinceDisabled = (System.currentTimeMillis() - disabledAt) / 50 + 1;
        this.ticks = (int) Math.max(0, this.ticks - ticksElapsedSinceDisabled);
        EVENTS.add(PacketInputListener.class, this);
        EVENTS.add(UpdateListener.class, this);
	}

	@Override
	protected void onDisable() {
		EVENTS.remove(UpdateListener.class, this);
        EVENTS.remove(PacketInputListener.class, this);
        this.disabledAt = System.currentTimeMillis();
	}

	@Override
	public void onUpdate() {
        if (this.cd) {
            this.ticks--;
            if (this.ticks <= 0) {
                disableCd();
            }
        } else {
            this.ticks++;
            if (this.ticks >= this.maxDuration.getValueI()) {
                enableCd();
            }
        }
	}

    private void disableCd() {
        this.cd = false;
        this.ticks = 0;
    }

    private void enableCd() {
        this.cd = true;
        this.ticks = this.cooldown.getValueI();
    }

    @Override
    public void onReceivedPacket(PacketInputEvent event) {
        if (!this.cdOnSetback.isChecked()) {
            return;
        }
        if (!(event.getPacket() instanceof PlayerPositionLookS2CPacket)) {
            return;
        }
        PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();

        Entity player = WurstClient.MC.cameraEntity;
        if (player != null) {
            player.sendSystemMessage(
                    Text.of("cd due to setback: " + packet.getTeleportId()),
                    null);
            enableCd();
        }
    }
}
