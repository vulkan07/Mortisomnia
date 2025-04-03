package me.barni.mortisomnia.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.paractivity.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ParactivityCommand {
    private enum MODES {ADD, FORCE_ADD, QUERY, RESET, CANCEL}
    private static final SuggestionProvider<ServerCommandSource> SUGGESTIONS = ((context, builder) -> suggestActivities(builder));

    private static CompletableFuture<Suggestions> suggestActivities(SuggestionsBuilder builder) {
//       for (String activity : Paractivities.getActivityNames()) {
//            builder.suggest(activity);
//        }
        CommandSource.suggestMatching(Paractivities.getActivityNames(), builder);
        return builder.buildFuture();
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            dispatcher.register(
                literal("paractivity").requires(src -> src.hasPermissionLevel(2))
                        .then(literal("add").then(argument("name", StringArgumentType.string()).suggests(SUGGESTIONS)
                            .then(argument("forced", BoolArgumentType.bool()) .executes(ctx -> execute(ctx, BoolArgumentType.getBool(ctx, "forced") ? MODES.FORCE_ADD : MODES.ADD))))
                            .then(argument("name", StringArgumentType.string()) .executes(ctx -> execute(ctx, MODES.ADD)) ) )
                        .then(literal("reset") .executes(ctx -> execute(ctx, MODES.RESET)))
                        .then(literal("query") .executes(ctx -> execute(ctx, MODES.QUERY)))
                        .then(literal("stop") .then(argument("name", StringArgumentType.string()) .executes(ctx -> execute(ctx, MODES.CANCEL))
                        )));});
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, MODES mode) throws CommandSyntaxException {

        PlayerEntity player = ctx.getSource().getPlayer();

        if (player == null) {
            var invalid = Text.literal("This must be run as a player!");
            throw new SimpleCommandExceptionType(invalid).create();
        }

        switch (mode) {
            case QUERY -> query(player);
            case ADD -> handleAdd(player, StringArgumentType.getString(ctx, "name"), false);
            case FORCE_ADD -> handleAdd(player, StringArgumentType.getString(ctx, "name"), true);
            case RESET -> ParaController.getInstance().reset();
            case CANCEL -> ctx.getSource().sendError(Text.of("Cancelling a paractivity via command is not implemented yet!"));
        }
        return 1;
    }


    private static void handleAdd(PlayerEntity player, String name, boolean force) throws CommandSyntaxException {
        String result = add(player, name, force);
        if (result != null) {
            throw new SimpleCommandExceptionType(Text.literal("[ParaController] "+result)).create();
        }
    }

    private static String add(PlayerEntity player, String name, boolean force) {
        ParactivityFactory factory = Paractivities.REGISTRY.get(Identifier.of(Mortisomnia.MOD_ID,name));
        if (factory == null) {
            return "Unknown type: " + name;
        }
        Paractivity a = factory.create(player);

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
