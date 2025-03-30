package me.barni.mortisomnia.paractivity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class Paractivity {

    public abstract ParaResult tick();
    public abstract String getName();

    protected PlayerEntity player;
    protected World world;

    protected boolean finished, success;

    // Fallback values, each activity should override these
    protected int controllerCoolDown = 1; // After an activity, the ParaController will not add anything 5x seconds on its own
    protected int typeCoolDown = 4; // After an activity, the controller will not add that type again for 5x seconds
    protected int hauntToRemove = 10; // Remove this amount of haunt from the player when starting, but restore if activity fails
    protected int requiredHaunt = 100; // The minimum haunt a player needs for an activity to occur

    protected void setMeta(int controllerCoolDown, int typeCoolDown, int hauntToRemove, int requiredHaunt) {
        this.controllerCoolDown = controllerCoolDown;
        this.typeCoolDown = typeCoolDown;
        this.hauntToRemove = hauntToRemove;
        this.requiredHaunt = requiredHaunt;
    }

    public Paractivity(PlayerEntity player) {
        this.player = player;
        this.world = player.getWorld();
        this.finished = false;
    }

    /** Common init function called for all activities first, then it calls {@link #customInit()} which must be
     * implemented by each activity themselves
     * @param ignoreHauntRequired if set, the player does not need to have enough haunt to add this activity
     * @return {@link ParaResult#success()} if all initialization checks were successful. otherwise {@link  ParaResult#fail(String reason)}
     * */
    public final ParaResult init(boolean ignoreHauntRequired) {
        if (!ignoreHauntRequired && requiredHaunt > 0 && ParaController.getInstance().getPlayerHaunt(player) < requiredHaunt) {
            return ParaResult.fail("Not enough haunt");
        }
        var result = this.customInit();

        if (result.getType() == ParaResult.Type.SUCCESS) // only remove haunt if initialization was successful
            ParaController.getInstance().decrementPlayerHaunt(player, hauntToRemove);

        return result;
    }

    protected abstract ParaResult customInit();

    public void cancel() {
        this.finished = true;
        this.success = false;
        ParaController.getInstance().incrementPlayerHaunt(player, hauntToRemove); // Restore haunt score if failed
    }

    protected ParaResult end() {
        this.finished = true;
        this.success = true;
        ParaController.getInstance().addCoolDown(controllerCoolDown);
        ParaController.getInstance().addTypeCoolDown(getName(), typeCoolDown);
        return ParaResult.end();
    }

    public boolean permitsParactivity(Paractivity other) {
        return true;
    }
    public boolean isFinished() {
        return this.finished;
    }
    public PlayerEntity getPlayer() {
        return player;
    }
    public World getWorld() {
        return world;
    }
}
