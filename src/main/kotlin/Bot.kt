import core.command.CommandManager
import core.database.settings
import core.listener.MessageListener
import core.listener.UserListener
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.any
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.ButtonInteraction
import dev.kord.core.entity.interaction.GuildChatInputCommandInteraction
import dev.kord.core.entity.interaction.SelectMenuInteraction
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.BanRemoveEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory

class Bot : KoinComponent {

    private val commandManager: CommandManager by inject()
    private val messageListener: MessageListener by inject()
    private val userListener: UserListener by inject()

    private val logger = LoggerFactory.getLogger("Vanced Helper")

    suspend fun start() {
        val kord = Kord(config.token)

        with(commandManager) {
            addCommands()
            runPreInit()
        }

        kord.on<InteractionCreateEvent> {
            when (interaction) {
                is GuildChatInputCommandInteraction -> commandManager.respondCommandInteraction(interaction as GuildChatInputCommandInteraction)
                is SelectMenuInteraction -> commandManager.respondSelectMenuInteraction(interaction as SelectMenuInteraction)
                is ButtonInteraction -> commandManager.respondButtonInteraction(interaction as ButtonInteraction)
                else -> return@on
            }
        }

        kord.on<MessageCreateEvent> {
            with(messageListener) {
                filterMessageSpam(message)
                filterSingleMessageEmoteSpam(message)
                runDevCommands(message, commandManager, kord, logger)
            }
        }

        kord.on<MemberUpdateEvent> {
            val oldWasBooster = old?.roles?.any { it.id == Snowflake(settings.boosterRoleId) }
                ?: return@on
            val newIsBooster = member.roles.any { it.id == Snowflake(settings.boosterRoleId) }

            when {
                !oldWasBooster && newIsBooster -> {}
                oldWasBooster && !newIsBooster -> {
                    userListener.onMemberUnboostGuild(member)
                }
            }
        }

        kord.on<MemberLeaveEvent> {
            userListener.onMemberLeaveGuild(getGuild(), logger)
        }

        kord.on<BanAddEvent> {
            val ban = getBan()
            userListener.onMemberBan(ban.getUser(), user, ban.reason)
        }

        kord.on<BanRemoveEvent> {
            userListener.onMemberUnban(user)
        }

        kord.login {
            if (settings.logChannelId == 0L) {
                return@login
            }

            val channel = kord.getGuild(config.guildSnowflake)?.getChannel(Snowflake(settings.logChannelId))
            val messageChannel = channel as? MessageChannel ?: return

            messageChannel.createMessage("I just started!")
        }
    }

}