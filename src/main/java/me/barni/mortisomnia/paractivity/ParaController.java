package me.barni.mortisomnia.paractivity;

import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.Utils;
import me.barni.mortisomnia.paractivity.activities.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.ArrayList;
import java.util.HashMap;

import static me.barni.mortisomnia.Mortisomnia.RANDOM;

public class ParaController {
    private static ParaController INSTANCE;

    public static ParaController getInstance() {
        if (INSTANCE == null)
            INSTANCE = new ParaController();
        return INSTANCE;
    }

    private ParaController() {
    }

    public static int toControllerTime(int min, int sec) {
        return (min * 60 + sec) / 5;
    }

    private final ArrayList<Paractivity> activities = new ArrayList<>();
    private final Utils.TickTimer timer = new Utils.TickTimer(20 * 5); // 5-second delay for each tick

    private final HashMap<String, Integer> typeCoolDownTimers = new HashMap<>();
    private int coolDownTimer = 0;

    public void addCoolDown(int amount) {
        this.coolDownTimer = Math.max(0, amount);
    }

    public void addTypeCoolDown(String name, int amount) {
        if (amount < 1) return;
        this.typeCoolDownTimers.put(name, amount);
    }

    public String[] getCurrentActivitiesNames() {
        String[] names = new String[activities.size()];
        for (int i = 0; i < activities.size(); i++) {
            names[i] = activities.get(i).getName();
        }
        return names;
    }

    private void updatePlayerHaunt(PlayerEntity player) {
        World world = player.getWorld();
        int score = 0;
        int v; // for debug printing

        // 1. By progress of time
        int day = (int) (world.getTimeOfDay() / 24000);
        if (day > 0) { // no haunt on first day
            v = RANDOM.nextInt((day+1) / 2);
            if( v > 0)
                Mortisomnia.LOGGER.warn("+time: {}", v);
            score += v;
        }
        // 2. Cave score
        int cave = Utils.isPlayerInCave(player);
        if (cave > 130) {
            v = MathHelper.clamp(cave / 100, 0, 4);
            Mortisomnia.LOGGER.warn("+cave: {}", v);
            score += v;
        }
        // 3. Light level around player
        float light = Utils.getAverageLightnessAroundPlayer(player);
        if (light < 1) {
            v = RANDOM.nextInt(3);
            score += v;
            Mortisomnia.LOGGER.warn("+light: {}", v);
        }
        // +. Alone score (only in multiplayer)
        if (Utils.isPlayerAlone(player, 128, true) && world.getPlayers().size() > 1) {
            v = RANDOM.nextInt(6);
            score += v;
            Mortisomnia.LOGGER.warn("+alone: {}", v);
        }
        // 4. Full moon doubles score (except first night
        if (world.getMoonPhase() == 1 && world.getTime() > 240000) {
            v = RANDOM.nextInt(3) * (world.getMoonPhase() == 1 ? 2 : 1);
            Mortisomnia.LOGGER.warn("+night: {}", v);
            score += v;
        }

        incrementPlayerHaunt(player, score);
    }

    public void init() {
        Mortisomnia.LOGGER.info("ParaController initialized");
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    public boolean addHandledParactivity(Paractivity activity, boolean force, boolean needsHaunt) {
        ParaResult result = addParactivity(activity, force, needsHaunt);
        if (result.getType() == ParaResult.Type.FAIL) {
            Mortisomnia.LOGGER.info("[ParaController] Failed to add {} ({})", activity.getName(), result.getMessage());
            return false;
        }
        Mortisomnia.LOGGER.info("[ParaController] added {}", activity.getName());
        return true;
    }

    public ParaResult addParactivity(Paractivity activity, boolean force, boolean needsHaunt) {
        PlayerEntity player = activity.getPlayer();

        if (player.getWorld().isClient()) {
            return ParaResult.fail("cannot add activity on client side!");
        }


        // if NOT overworld, fail
        if (!player.getWorld().getDimensionEntry().getIdAsString().equals(DimensionTypes.OVERWORLD_ID.toString())) {
            return ParaResult.fail("activities can only occur in Overworld!");
        }

        if (!force) {
            if (typeCoolDownTimers.get(activity.getName()) != null) {
                return ParaResult.fail("this type is under cool down! (" + typeCoolDownTimers.get(activity.getName()) * 5 + "s left)");
            }
            for (var a2 : activities) {
                if (!a2.permitsParactivity(activity)) {
                    return ParaResult.fail("not permitted by " + a2.getName());
                }
            }
        }
        ParaResult result = activity.init(!needsHaunt);

        if (result.getType() == ParaResult.Type.FAIL)
            return ParaResult.fail(result.getMessage());

        activities.add(activity);
        return ParaResult.success();
    }

    public void tick(MinecraftServer minecraftServer) {
        // Tick paractivities
        var iterator = activities.iterator();
        while (iterator.hasNext()) {

            var activity = iterator.next();
            ParaResult result = activity.tick();

            if (result.getType() == ParaResult.Type.FAIL) {
                Mortisomnia.LOGGER.info("[ParaController] {} failed and removed ({})", activity.getName(), result.getMessage());
                iterator.remove();
                continue; // prevent double remove (when an activity calls cancel then returns FAIL
            }
            if (activity.isFinished() || result.getType() == ParaResult.Type.FINISHED) {
                Mortisomnia.LOGGER.info("[ParaController] {} ended", activity.getName());
                iterator.remove();
            }
        }

        // Every 5s perform "think" tick
        // Handles cooldown timers and player haunt
        if (timer.tick()) {
            for (var world : minecraftServer.getWorlds())
                if (world.getDimensionEntry().getIdAsString().equals(DimensionTypes.OVERWORLD_ID.toString())) {
                    for (var player : world.getPlayers()) {

                        // Decrement timer for all paractivities
                        var iterator2 = typeCoolDownTimers.keySet().iterator();
                        while (iterator2.hasNext()) {
                            String key = iterator2.next();
                            int v = typeCoolDownTimers.get(key);
                            v--;
                            if (v <= 0)
                                iterator2.remove();
                            else
                                typeCoolDownTimers.put(key, v);
                        }

                        if (RANDOM.nextInt(7) == 0) {
                            updatePlayerHaunt(player);
                        }

                        // Handle cooldown
                        if (coolDownTimer > 0) {
                            coolDownTimer--;
                            return;
                        }

                        // Add random paractivity
                        Paractivity a = null;
                        int choice = RANDOM.nextInt(100);
                        switch (choice) {
                            case 0 -> a = new SpookSoundParactivity(player);
                            case 1 -> a = new DoorParactivity(player);
                            case 2 -> a = new DoorToggleParactivity(player);
                            case 3 -> a = new TorchOffParactivity(player);
                            case 4 -> a = new ScareCrowParactivity(player);
                            case 5 -> a = new WeepingAngelParactivity(player);
                            case 6 -> a = new LightExtinguishParactivity(player);
                            case 7 -> a = new LightFlickerParactivity(player);
                        }
                        if (a != null)
                            addHandledParactivity(a, false, true);

                    }
                    return; // no multiworld support (yet?)
                }
        }
    }




    public void decrementPlayerHaunt(PlayerEntity player, int amount) {
        if (player.getWorld().isClient()) {
            Mortisomnia.LOGGER.error("[ParaController] Tried decrementing Haunt on CLIENT");
            return;
        }
        incrementPlayerHaunt(player, -amount);
    }
    public void incrementPlayerHaunt(PlayerEntity player, int amount) {
        if (player.getWorld().isClient()) {
            Mortisomnia.LOGGER.error("[ParaController] Tried incrementing Haunt on CLIENT");
            return;
        }
        setPlayerHaunt(player, getPlayerHaunt(player)+amount);
    }
    public void setPlayerHaunt(PlayerEntity player, int value) {
        if (player.getWorld().isClient()) {
            Mortisomnia.LOGGER.error("[ParaController] Tried setting Haunt on CLIENT");
            return;
        }
        if (value == getPlayerHaunt(player)) return;
        Utils.getPlayerPersistentData(player).putInt("haunt", value);
        player.sendMessage(Text.literal(String.valueOf(value)).formatted(Formatting.AQUA), true);
    }
    public int getPlayerHaunt(PlayerEntity player) {
        if (player.getWorld().isClient()) {
            Mortisomnia.LOGGER.error("[ParaController] Tried getting Haunt on CLIENT");
            return -6969;
        }
        return Utils.getPlayerPersistentData(player).getInt("haunt");
    }

}
