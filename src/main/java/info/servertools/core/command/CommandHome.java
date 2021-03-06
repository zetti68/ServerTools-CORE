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
package info.servertools.core.command;

import info.servertools.core.feature.Features;
import info.servertools.core.feature.HomeHandler;
import info.servertools.core.util.Location;
import info.servertools.core.util.PlayerUtils;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

@Command(
        name = "home",
        opRequired = false,
        requiredFeatures = { HomeHandler.class }
)
public class CommandHome extends STCommand {

    private final HomeHandler homeHandler;

    public CommandHome() {
        this.homeHandler = Features.getService(HomeHandler.class).get();
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "/" + getCommandName() + " [set|delete]";
    }

    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        final EntityPlayerMP player = requirePlayer(sender);
        if (args.length == 0) {
            final Location home = homeHandler.getHome(player.getPersistentID())
                    .orElseThrow(() -> new CommandException("You don't have a home set"));
            PlayerUtils.teleportPlayer(player, home);
            player.addChatMessage(new ChatComponentText("Teleported home"));
        } else if (args.length == 1) {
            if ("set".equals(args[0])) {
                HomeHandler.EditHomeResult result = homeHandler.setHome(player.getPersistentID(), new Location(player));
                player.addChatMessage(result.toChat());
            } else if ("delete".equals(args[0])) {
                HomeHandler.EditHomeResult result = homeHandler.deleteHome(player.getPersistentID());
                player.addChatMessage(result.toChat());
            } else {
                throw new WrongUsageException(getCommandUsage(sender));
            }
        } else {
            throw new WrongUsageException(getCommandUsage(sender));
        }
    }
}
