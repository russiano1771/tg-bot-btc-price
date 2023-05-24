package DGK.btcbankertest.initializer;

import DGK.btcbankertest.service.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;



// SLF4J Данная аннотация позволяет записывать информацию о выполнении кода в логи
@Slf4j //SLF4J расшифровывается как S реализует F академию для J ava.
// Он обеспечивает простую абстракцию всех каркасов логирования в Java. Таким образом, он позволяет пользователю работать с любой из сред ведения журналов,
@Component
public class Bot_Initializer {

    @Autowired //spring автоматически подключает
    TelegramBot bot;

    // слушатель событий
    @EventListener(ContextRefreshedEvent.class)
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage()); // в случае возникновения ошибки, записываем в логи соответствующую информацию
        }
    }
}
