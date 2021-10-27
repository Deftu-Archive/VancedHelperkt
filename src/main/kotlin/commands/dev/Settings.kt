package commands.dev

import core.command.CommandContext
import core.command.base.BaseCommand
import core.database.*
import core.database.data.MongoMutableList
import core.util.botOwners
import core.wrapper.applicationcommand.CustomApplicationCommandCreateBuilder
import core.wrapper.applicationcommand.CustomApplicationCommandPermissionBuilder
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.interaction.channel
import dev.kord.core.entity.interaction.role
import dev.kord.rest.builder.interaction.channel
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.role
import dev.kord.rest.builder.interaction.subCommand

class Settings : BaseCommand(
    commandName = "settings",
    commandDescription = "Configure settings",
    defaultPermissions = false
) {

    override suspend fun execute(
        ctx: CommandContext
    ) {
        val subCommand = ctx.subCommand
        val subCommandGroup = ctx.subCommandGroup

        when {
            subCommand != null -> {
                when (subCommand.name) {
                    "logchannel" -> configureLogChannel(ctx)
                    "modlogchannel" -> configureModLogChannel(ctx)
                    "errorchannel" -> configureErrorChannel(ctx)
                    "muterole" -> configureMuteRole(ctx)
                    "boosterrole" -> configureBoosterRole(ctx)
                }
            }
            subCommandGroup != null -> {
                when (subCommandGroup.groupName) {
                    "moderators" -> {
                        when (subCommandGroup.name) {
                            "add" -> addModerator(ctx)
                            "remove" -> removeModerator(ctx)
                        }
                    }
                    "quoters" -> {
                        when (subCommandGroup.name) {
                            "add" -> addQuoteRole(ctx)
                            "remove" -> removeQuoteRole(ctx)
                        }
                    }
                    "colourmes" -> {
                        when (subCommandGroup.name) {
                            "add" -> addColourMeRole(ctx)
                            "remove" -> removeColourMeRole(ctx)
                        }
                    }
                }
            }
        }
    }

    override suspend fun commandOptions() =
        CustomApplicationCommandCreateBuilder(
            arguments = {
                subCommand(
                    name = "muterole",
                    description = "Set the mute role",
                    builder = {
                        role(
                            name = "role",
                            description = "Role to assign",
                            builder = {
                                required = true
                            }
                        )
                    }
                )
                subCommand(
                    name = "logchannel",
                    description = "Set the log channel",
                    builder = {
                        channel(
                            name = "channel",
                            description = "Channel to use as the log channel",
                            builder = {
                                required = true
                            }
                        )
                    }
                )
                subCommand(
                    name = "modlogchannel",
                    description = "Set the moderator action log channel",
                    builder = {
                        channel(
                            name = "channel",
                            description = "Channel to use as the moderator action log channel",
                            builder = {
                                required = true
                            }
                        )
                    }
                )
                subCommand(
                    name = "errorchannel",
                    description = "Set the error log channel",
                    builder = {
                        channel(
                            name = "channel",
                            description = "Channel to use as the error log channel",
                            builder = {
                                required = true
                            }
                        )
                    }
                )
                group(
                    name = "moderators",
                    description = "Edit moderators",
                    builder = {
                        subCommand(
                            name = "add",
                            description = "Add a moderator",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to add to moderators",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                        subCommand(
                            name = "remove",
                            description = "Remove a moderator",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to remove from moderators",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                    }
                )
                group(
                    name = "quoters",
                    description = "Edit roles that can add quotes",
                    builder = {
                        subCommand(
                            name = "add",
                            description = "Add allowed quote role",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to add to allowed quote roles",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                        subCommand(
                            name = "remove",
                            description = "Removed allowed quote role",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to remove from allowed quote roles",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                    }
                )
                group(
                    name = "colourmes",
                    description = "Edit roles that can use colourme",
                    builder = {
                        subCommand(
                            name = "add",
                            description = "Add allowed colourme role",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to add to allowed colourme roles",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                        subCommand(
                            name = "remove",
                            description = "Removed allowed colourme role",
                            builder = {
                                role(
                                    name = "role",
                                    description = "Role to remove from allowed colourme roles",
                                    builder = {
                                        required = true
                                    }
                                )
                            }
                        )
                    }
                )
            }
        )

    override fun commandPermissions() =
        CustomApplicationCommandPermissionBuilder(
            permissions = {
                for (owner in botOwners) {
                    user(
                        id = Snowflake(owner),
                        allow = true
                    )
                }
            }
        )

    private suspend fun configureLogChannel(ctx: CommandContext) {
        val channel = ctx.args["channel"]!!.channel()

        logChannelId = channel.id.value.toLong()
        ctx.respondPublic {
            content = "Successfully updated the log channel"
        }
    }

    private suspend fun configureModLogChannel(ctx: CommandContext) {
        val channel = ctx.args["channel"]!!.channel()

        modLogChannelId = channel.id.value.toLong()
        ctx.respondPublic {
            content = "Successfully updated the moderator action log channel"
        }
    }

    private suspend fun configureErrorChannel(ctx: CommandContext) {
        val channel = ctx.args["channel"]!!.channel()

        errorChannelId = channel.id.value.toLong()
        ctx.respondPublic {
            content = "Successfully updated the error log channel"
        }
    }

    private suspend fun configureMuteRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        muteRoleId = role.id.value.toLong()
        ctx.respondPublic {
            content = "Successfully updated the mute role"
        }
    }

    private suspend fun configureBoosterRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        boosterRoleId = role.id.value.toLong()
        ctx.respondPublic {
            content = "Successfully updated the booster role"
        }
    }

    private suspend fun addModerator(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        moderatorRoleIds.addWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "moderators",
            mention = role.mention
        )
    }

    private suspend fun removeModerator(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        moderatorRoleIds.removeWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "moderators",
            mention = role.mention
        )
    }

    private suspend fun addQuoteRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        allowedQuoteRoleIds.addWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "quoters",
            mention = role.mention
        )
    }

    private suspend fun removeQuoteRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        allowedQuoteRoleIds.removeWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "quoters",
            mention = role.mention
        )
    }

    private suspend fun addColourMeRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        allowedColourMeRoleIds.addWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "fruities",
            mention = role.mention
        )
    }

    private suspend fun removeColourMeRole(ctx: CommandContext) {
        val role = ctx.args["role"]!!.role()

        allowedColourMeRoleIds.removeWithChecks(
            ctx = ctx,
            element = role.id.value.toLong(),
            itemName = "fruities",
            mention = role.mention
        )
    }

    private suspend fun <E, TDocument> MongoMutableList<E, TDocument>.addWithChecks(
        ctx: CommandContext,
        element: E,
        itemName: String,
        mention: String
    ) {
        if (!contains(element)) {
            add(element)
            ctx.respondEphemeral {
                content = "Successfully added $mention to $itemName"
            }
        } else {
            ctx.respondEphemeral {
                content = "$mention already exists in $itemName"
            }
        }
    }

    private suspend fun <E, TDocument> MongoMutableList<E, TDocument>.removeWithChecks(
        ctx: CommandContext,
        element: E,
        itemName: String,
        mention: String
    ) {
        if (remove(element)) {
            ctx.respondEphemeral {
                content = "Successfully removed $mention from $itemName"
            }
        } else {
            ctx.respondEphemeral {
                content = "$mention doesn't exist in $itemName"
            }
        }
    }

}