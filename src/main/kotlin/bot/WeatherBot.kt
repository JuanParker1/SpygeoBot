package bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.*
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import data.remote.WEATHER_API_KEY
import data.remote.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val IS_DAY = 1

private const val GIF_WAITING_URL =
    "https://tenor.com/view/why-am-i-still-waiting-patiently-waiting-waiting-gif-12710222"
private const val BOT_TOKEN = "5348225518:AAHO6mTCfjLy3ObEeMYcgQ2T4ypVe8nqEzY"
private const val TIMEOUT_TIME = 30

class WeatherBot(private val weatherRepository: WeatherRepository) {

    private lateinit var country: String
    private var _chatId: ChatId.Id? = null
    private val chatId by lazy { requireNotNull(_chatId) }

    fun createBot(): Bot {
        return bot {
            token = BOT_TOKEN
            timeout = TIMEOUT_TIME
            logLevel = LogLevel.Network.Body

            dispatch {
                setUpCommands()
                setUpCallbacks()
            }
        }
    }

    private fun Dispatcher.setUpCallbacks() {
        callbackQuery(callbackData = "getMyLocation") {
            bot.sendMessage(chatId = chatId, text = "Send your location")
            location {
                CoroutineScope(Dispatchers.IO).launch {
                    val userCountryName = weatherRepository.getCountryNameByCoordinates(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        format = "json"
                    ).address.state

                    val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                        listOf(
                            InlineKeyboardButton.CallbackData(
                                text = "Yes, that's right.",
                                callbackData = "yes_label"
                            )
                        )
                    )
                    country = userCountryName

                    bot.sendMessage(
                        chatId = chatId,
                        text = "Your city - ${country}, right? \n If incorrect, drop the location again.",
                        replyMarkup = inlineKeyboardMarkup
                    )
                }
            }
        }

        callbackQuery(callbackData = "enterManually") {
            bot.sendMessage(chatId = chatId, text = "Enter your city")
            message(Filter.Text) {
                val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                    listOf(
                        InlineKeyboardButton.CallbackData(
                            text = "Yes, that's right.",
                            callbackData = "yes_label"
                        )
                    )
                )
                country = message.text.toString()
                bot.sendMessage(
                    chatId = chatId,
                    text = "Your city - - ${message.text}, right? \n If incorrect, drop the location again.",
                    replyMarkup = inlineKeyboardMarkup
                )
            }
        }

        callbackQuery(callbackData = "yes_label") {
            bot.apply {
                sendAnimation(chatId = chatId, animation = TelegramFile.ByUrl(GIF_WAITING_URL))
                sendMessage(chatId = chatId, text = "Let's find out your weather...")
                sendChatAction(chatId = chatId, action = ChatAction.TYPING)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val currentWeather = weatherRepository.getCurrentWeather(
                    apiKey = WEATHER_API_KEY,
                    queryCountry = country,
                    isAqiNeeded = "no"
                )
                bot.sendMessage(
                    chatId = chatId,
                    text = """
                            ☁ Overcast: ${currentWeather.current.cloud}
                            🌡 Temperature (degrees): ${currentWeather.current.tempDegrees}
                            🙎 Feels like: ${currentWeather.current.feelsLikeDegrees}
                            💧 Humidity: ${currentWeather.current.humidity}
                            🌪 Wind direction: ${currentWeather.current.windDirection}
                            🧭 Pressure: ${currentWeather.current.pressureIn}
                        """.trimIndent()
                )
                bot.sendMessage(
                    chatId = chatId,
                    text = "If you want to request the weather again, \n use the /weather command"
                )
                country = ""
            }
        }
    }

    private fun Dispatcher.setUpCommands() {
        command("start") {
            _chatId = ChatId.fromId(message.chat.id)
            bot.sendMessage(
                chatId = chatId,
                    text = "Hi! I'm a bot that can display the weather! \n To launch the bot, enter the command /weather"
            )
        }

        command("weather") {
            val inlineKeyboardMarkup = InlineKeyboardMarkup.create(
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Identify my city (for mobile devices)",
                        callbackData = "getMyLocation"
                    )
                ),
                listOf(
                    InlineKeyboardButton.CallbackData(
                        text = "Enter the city manually",
                        callbackData = "enterManually"
                    )
                )
            )
            bot.sendMessage(
                chatId = chatId,
                text = "In order for me to send you the weather, \n  I need to know your city.",
                replyMarkup = inlineKeyboardMarkup
            )
        }
    }
}