package edu.java.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.TelegramAnswer;
import edu.java.bot.model.UserMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class TelegramRequestServiceImpl implements TelegramRequestService {

    private static final TelegramAnswer FALLBACK_MESSAGE = new TelegramAnswer(
        Optional.of("Undefined command, see /help")
    );
    private final TelegramBot telegramBot;
    private final Map<String, Function<UserMessage, TelegramAnswer>> messageRoutes;
    private final Counter requestsCounter;

    public TelegramRequestServiceImpl(
        TelegramBot telegramBot,
        TelegramRequestRoutesService telegramRequestRoutesService,
        MeterRegistry meterRegistry
    ) {
        this.telegramBot = telegramBot;
        messageRoutes = Map.of(
            "/start", telegramRequestRoutesService::start,
            "/help", telegramRequestRoutesService::help,
            "/track", telegramRequestRoutesService::track,
            "/untrack", telegramRequestRoutesService::untrack,
            "/list", telegramRequestRoutesService::list
        );
        this.requestsCounter = meterRegistry.counter("telegram.requests.processed");
    }

    @Override
    public void processMessage(UserMessage message) {
        var route = findRoute(message.text());
        var answer = route
            .map(f -> f.apply(message))
            .orElse(FALLBACK_MESSAGE);
        answer.text().ifPresent(text -> {
            telegramBot.execute(new SendMessage(message.chatId(), text));
            requestsCounter.increment();
        });
    }

    private Optional<Function<UserMessage, TelegramAnswer>> findRoute(String text) {
        return messageRoutes.entrySet().stream()
            .filter(entry -> text.startsWith(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst();
    }
}
