/*
 * This file is a part of ServerTools <http://servertools.info>
 *
 * Copyright (c) 2014 ServerTools
 * Copyright (c) 2014 contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.servertools.core;

import info.servertools.core.commands.CommandMotd;
import info.servertools.core.commands.CommandPing;
import info.servertools.core.commands.CommandSilence;
import info.servertools.core.commands.CommandWhereIs;
import info.servertools.core.feature.Motd;
import info.servertools.core.feature.SilenceHandler;
import info.servertools.core.util.FileIO;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod(
        modid = ServerToolsCore.MOD_ID,
        name = ServerToolsCore.MOD_NAME,
        acceptableRemoteVersions = "*", // Don't require mod on client
        acceptedMinecraftVersions = '[' + ServerToolsCore.MC_VERSION + ']'
)
public final class ServerToolsCore {

    public static final String MOD_ID = "ServerTools-CORE";
    public static final String MOD_NAME = MOD_ID;
    public static final String MC_VERSION = "1.8";

    private static Path configDir;

    private static STConfig<CoreConfig> coreConfig;

    @Mod.EventHandler
    public void onPreInit(final FMLPreInitializationEvent event) throws IOException {
        final File modConfigDir = event.getModConfigurationDirectory();
        configDir = modConfigDir.toPath().resolve("ServerTools-CORE");
        Files.createDirectories(configDir);
        coreConfig = new STConfig<>(configDir.resolve("ServerTools-CORE.conf"), CoreConfig.class);

        CommandManager.init(configDir.resolve("commands.conf"));

        if (getConfig().getChat().isMotdEnabled()) {
            Motd motd = new Motd(configDir.resolve("motd.txt"));
            CommandMotd motdCommand = new CommandMotd("motd", motd);
            CommandManager.registerCommand(motdCommand);
        }

        SilenceHandler silenceHandler = new SilenceHandler(configDir.resolve("silenced.json"));
        CommandSilence silenceCommand = new CommandSilence(silenceHandler, "silence");
        CommandManager.registerCommand(silenceCommand);

        CommandManager.registerCommand(new CommandPing("ping"));
        CommandManager.registerCommand(new CommandWhereIs("whereis"));
    }

    @Mod.EventHandler
    public void onServerStarted(final FMLServerStartedEvent event) {
        CommandManager.register(MinecraftServer.getServer());
    }

    @Mod.EventHandler
    public void onServerStoping(final FMLServerStoppingEvent event) {
        FileIO.shutDown();
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static CoreConfig getConfig() {
        return coreConfig.getConfig();
    }
}