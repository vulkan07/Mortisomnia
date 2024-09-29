package me.barni.mortisomnia.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.barni.mortisomnia.paractivity.ParaController;
import me.barni.mortisomnia.paractivity.ParaResult;
import me.barni.mortisomnia.paractivity.Paractivity;
import me.barni.mortisomnia.paractivity.activities.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParactivityCommand {
    private enum MODES {ADD, FORCE_ADD, LIST, QUERY}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            dispatcher.register(
                literal("paractivity").requires(src -> src.hasPermissionLevel(2))
                        .then(literal("list")
                            .executes(ctx -> execute(ctx, MODES.LIST))
                        )
                        .then(literal("query")
                                .executes(ctx -> execute(ctx, MODES.QUERY))
                        )
                        .then(literal("add")
                            .then(argument("name", StringArgumentType.string())
                                .then(argument("force", BoolArgumentType.bool())
                                    .executes(ctx -> execute(ctx, BoolArgumentType.getBool(ctx, "force") ? MODES.FORCE_ADD : MODES.ADD))
                                )
                            )
                            .then(argument("name", StringArgumentType.string())
                                    .executes(ctx -> execute(ctx, MODES.ADD))
                            )
                        )
            );
        });
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, MODES mode) throws CommandSyntaxException {

        PlayerEntity player = ctx.getSource().getPlayer();

        if (player == null) {
            var invalid = Text.literal("This must be run as a player!");
            throw new SimpleCommandExceptionType(invalid).create();
        }

        switch (mode) {
            case LIST -> list(player);
            case QUERY -> query(player);
            case ADD -> handleAdd(player, StringArgumentType.getString(ctx, "name"), false);
            case FORCE_ADD -> handleAdd(player, StringArgumentType.getString(ctx, "name"), true);
        }
        return 1;
    }

    private static String[] listText = {
        "Available Paractivities:",
        " - WeepingAngel",
        " - Gazer",
        " - LightExtinguish",
        " - LightFlicker",
        " - CapturedLight",
        " - Torchoff",
        " - SpookSound",
        " - Door",
        " - DoorToggle",
        " - ScareCrow",
        " - FertilizerCapsule",
        " - Gazer",
        " - Creeper"
    };

    private static void list(PlayerEntity player) {
        for (String s : listText)
            player.sendMessage(Text.literal(s));
    }

    private static void handleAdd(PlayerEntity player, String name, boolean force) throws CommandSyntaxException {
        String result = add(player, name, force);
        if (result != null) {
            throw new SimpleCommandExceptionType(Text.literal("[ParaController] "+result)).create();
        }
    }

    private static String add(PlayerEntity player, String name, boolean force) {
        Paractivity a;
        switch (name.toLowerCase()) {
            case "weepingangel" -> a = new WeepingAngelParactivity(player);
            case "lightextinguish" -> a = new LightExtinguishParactivity(player);
            case "lightflicker" -> a = new LightFlickerParactivity(player);
            case "capturedlight" -> a = new CapturedLightParactivity(player);
            case "spooksound" -> a = new SpookSoundParactivity(player);
            case "torchoff" -> a = new TorchOffParactivity(player);
            case "door" -> a = new DoorParactivity(player);
            case "doortoggle" -> a = new DoorToggleParactivity(player);
            case "scarecrow" -> a = new ScareCrowParactivity(player);
            case "fertilizercapsule" -> a = new FertilizerCapsuleParactivity(player);
            case "killfoliage" -> a = new KillFoliageParactivity(player);
            case "gazer" -> a = new GazerParactivity(player);
            case "creeper" -> a = new CreeperParactivity(player);
            case "cave_spook" -> a = new CaveSpookParactivity(player);
            default -> { return "Unknown type: " + name; }
        }
        var result = ParaController.getInstance().addParactivity(a, force, false);
        if (result.getType() == ParaResult.Type.FAIL)
           return "Failed to add activity: " + result.getMessage();


        query(player);

        return null; // no error message = success
    }

    private static void query(PlayerEntity player) {
        String[] names = ParaController.getInstance().getCurrentActivitiesNames();
        if (names.length == 0) {
            player.sendMessage(Text.literal("[ParaController] no activities currently happening"));
            return;
        }
        player.sendMessage(Text.literal("[ParaController] currently happening activities:"));
        int i = names.length;
        for (String n : names){
            player.sendMessage(Text.literal("  " + (i) + ". " + n));
            i--;
        }
    }
}
