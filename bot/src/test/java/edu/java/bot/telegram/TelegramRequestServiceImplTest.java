package edu.java.bot.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.model.TelegramAnswer;
import edu.java.bot.model.UserMessage;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelegramRequestServiceImplTest {

    private final TelegramAnswer exampleAnswer = new TelegramAnswer(Optional.of("Answer"));
    private final Long chatId = 0L;
    private final ArgumentMatcher<SendMessage> argumentMatcher = sendMessage ->
        sendMessage.getParameters().get("chat_id").equals(0L) &&
        sendMessage.getParameters().get("text").equals(exampleAnswer.text().get());
    private TelegramBot telegramBot;
    private TelegramRequestRoutesService telegramRequestRoutesService;
    private TelegramRequestService telegramRequestService;

    @BeforeEach
    void setUp() {
        telegramBot = mock(TelegramBot.class);
        telegramRequestRoutesService = mock(TelegramRequestRoutesService.class);
        var meterRegistry = mock(MeterRegistry.class);
        when(meterRegistry.counter(any())).thenReturn(mock(Counter.class));
        telegramRequestService = new TelegramRequestServiceImpl(
            telegramBot,
            telegramRequestRoutesService,
            meterRegistry
        );

    }

    @Test
    void processStart() {
        when(telegramRequestRoutesService.start(any())).thenReturn(exampleAnswer);

        telegramRequestService.processMessage(new UserMessage("/start", chatId));

        verify(telegramBot).execute(argThat(argumentMatcher));
    }

    @Test
    void processHelp() {
        when(telegramRequestRoutesService.help(any())).thenReturn(exampleAnswer);

        telegramRequestService.processMessage(new UserMessage("/help", chatId));

        verify(telegramBot).execute(argThat(argumentMatcher));
    }

    @Test
    void processTrack() {
        when(telegramRequestRoutesService.track(any())).thenReturn(exampleAnswer);

        telegramRequestService.processMessage(new UserMessage("/track", chatId));
    }

    @Test
    void processUntrack() {
        when(telegramRequestRoutesService.untrack(any())).thenReturn(exampleAnswer);

        telegramRequestService.processMessage(new UserMessage("/untrack", chatId));
        verify(telegramBot).execute(argThat(argumentMatcher));
    }

    @Test
    void processList() {
        when(telegramRequestRoutesService.list(any())).thenReturn(exampleAnswer);

        telegramRequestService.processMessage(new UserMessage("/list", chatId));
        verify(telegramBot).execute(argThat(argumentMatcher));
    }

    @Test
    void processBlaBla() {
        telegramRequestService.processMessage(new UserMessage("/blabla", chatId));
        verify(telegramBot).execute(argThat(sendMessage -> sendMessage.getParameters().get("text")
            .equals("Undefined command, see /help")));
    }
}
