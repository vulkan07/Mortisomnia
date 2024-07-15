package me.barni.mortisomnia.datagen;


import me.barni.mortisomnia.command.HauntScoreCommand;
import me.barni.mortisomnia.command.ParactivityCommand;


public class MortisomniaCommands {


    public static void registerCommands() {
        HauntScoreCommand.register();
        ParactivityCommand.register();
    }

}
