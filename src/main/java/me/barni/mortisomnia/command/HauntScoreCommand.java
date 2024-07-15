package me.barni.mortisomnia.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.barni.mortisomnia.Mortisomnia;
import me.barni.mortisomnia.paractivity.ParaController;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class HauntScoreCommand {
    private enum MODES {SET, ADD, REMOVE, GET}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, registrationEnvironment) -> {
            dispatcher.register(
                literal("haunt").requires(src -> src.hasPermissionLevel(2))
                        .then(literal("set")
                            .then(argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> execute(ctx, MODES.SET))
                            )
                        )
                        .then(literal("add")
                            .then(argument("amount", IntegerArgumentType.integer())
                                .executes(ctx -> execute(ctx, MODES.ADD))
                            )
                        )
                        .then(literal("remove")
                                .then(argument("amount", IntegerArgumentType.integer())
                                        .executes(ctx -> execute(ctx, MODES.REMOVE))
                                )
                        )
                        .then(literal("get")
                            .executes(ctx -> execute(ctx, MODES.GET))
                        )
            );
        });
    }

    private static int execute(CommandContext<ServerCommandSource> ctx, MODES mode) throws CommandSyntaxException {
        int value = 0;
        if (mode != MODES.GET)
            value = IntegerArgumentType.getInteger(ctx, "amount");

        PlayerEntity player = ctx.getSource().getPlayer();

        if (player == null) {
            var invalid = Text.literal("This must be run as a player!");
            throw new SimpleCommandExceptionType(invalid).create();
        }

        switch (mode) {
            case SET -> ParaController.getInstance().setPlayerHaunt(player, value);
            case ADD -> ParaController.getInstance().incrementPlayerHaunt(player, value);
            case REMOVE -> ParaController.getInstance().decrementPlayerHaunt(player, value);
            case GET -> player.sendMessage(Text.literal(String.valueOf(ParaController.getInstance().getPlayerHaunt(player))), true);
        }

        return 1;
    }
}
