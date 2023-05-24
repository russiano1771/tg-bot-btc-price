package DGK.btcbankertest.service;

import DGK.btcbankertest.config.BotConfig;
import DGK.btcbankertest.model.User;
import DGK.btcbankertest.repository.UserRepository;


import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// SLF4J Данная аннотация позволяет записывать информацию о выполнении кода в логи
@Slf4j //SLF4J расшифровывается как S реализует F академию для J ava.
// Он обеспечивает простую абстракцию всех каркасов логирования в Java. Таким образом, он позволяет пользователю работать с любой из сред ведения журналов,
@Component // аннотация автоматически создает экземпляр класса
public class TelegramBot extends TelegramLongPollingBot { // обязательное расширение для телеграм бота

    @Autowired
      private UserRepository userRepository;
      final BotConfig config;


    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start", "get started!"));
        listOfCommand.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommand.add(new BotCommand("/deletedata", "delete your data"));
        listOfCommand.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommand.add(new BotCommand("/settings", "settings your preference"));
        listOfCommand.add(new BotCommand("/testcommand", "testcommand"));
        listOfCommand.add(new BotCommand("/register", "register"));

        try {
            this.execute( new SetMyCommands(listOfCommand, new BotCommandScopeDefault(), null)); // устанавливаем наши команды
        } catch (TelegramApiException e) {
            log.error("Error setting bots command list: " + e.getMessage()); // при возникновении ошибки, запишем ее в наш журнал с логами
        }
    }

    @Override // username
    public String getBotUsername() {
        return config.getBotName(); // получаем имя бота из класса конфиг
    }

    @Override // token
    public String getBotToken() {
        return config.getToken(); // получаем token бота из класса конфиг
    }
//    private void startCommandReceived ( long chatId, String name){
//        String answer = EmojiParser.parseToUnicode("Привет, дорогой "+ name + " Как твои дела?" + ":blush:");
//        log.info("RepliedUser: " + name + " /// " + " TextMessage: " + answer); // записываем в журнал кому мы ответили и что за текст мы ответили
//        sendMessage(chatId, answer);
//    }

    private void startCommandReceived( long chatId, String name){
        String answer = "Приветствуем тебя, дорогой %s \n\n" +
                "Купить и продать криптовалюту \n" +
                "Личный кошелёк внутри бота.\n" +
                "Деньги за отзывы и не только.\n" +
                "Реферальная система.\n" +
                "Быстро, удобно, выгодно.\n";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(String.format(answer, name));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(); // создаем tgобъект кнопок
        List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons2 = new ArrayList<>(); // затем добавим этот список в список со списками




        // первый ряд
        InlineKeyboardButton buyButton = new InlineKeyboardButton();
        buyButton.setText("Купить");
        buyButton.setCallbackData("BUY_BUTTON");

        InlineKeyboardButton sellButton = new InlineKeyboardButton();
        sellButton.setText("Продать");
        sellButton.setCallbackData("SELL_BUTTON");





        // второй ряд
        InlineKeyboardButton walletButton = new InlineKeyboardButton();
        walletButton.setText("Кошелек");
        walletButton.setCallbackData("WALLET_BUTTON"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь

        InlineKeyboardButton referenceButton = new InlineKeyboardButton();
        referenceButton.setText("Рефералка");
        referenceButton.setCallbackData("REFERENCE_BUTTON");

        InlineKeyboardButton myAccount = new InlineKeyboardButton();
        myAccount.setText("Личный кабинет");
        myAccount.setCallbackData("ACCOUNT_BUTTON");

        buttons.add(buyButton);
        buttons.add(sellButton);

        buttons2.add(referenceButton);
        buttons2.add(walletButton);
        buttons2.add(myAccount);


        inlineRows.add(buttons);
        inlineRows.add(buttons2);

        markupInline.setKeyboard(inlineRows);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }

    }
    private void helpCommandReceived (long chatId, String name){
        String help = "This bot has created to demonstration goal \n\n" +
                "You can execute commands from the main menu on the left or by typing a command: \n"+
                "Type /start to see a welcome message \n"+
                "Type /mydata to see data stored about yourself \n"+
                "Type /help to see this message again"
                ;
        log.info("RepliedUser: " + name + " /// " + " TextMessage: " + help); // записываем в журнал кому мы ответили и что за текст мы ответили
        sendMessage(chatId, help);
    }
    private void myDataMethod(long chatId, String name){
        String data = "Тест дата метод";
        log.info("RepliedUser: " + name + " /// " + " TextMessage: " + data); // записываем в журнал кому мы ответили и что за текст мы ответили
        sendMessage(chatId, data);
    }
    public void register(long chatId){
        SendMessage message = new SendMessage(); // создаем объект сообщение
        message.setChatId(String.valueOf(chatId));
        message.setText("Do you really want to register?");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(); // создаем tgобъект кнопок
        List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>(); // создаем список со списками
        List<InlineKeyboardButton> buttons = new ArrayList<>(); // затем добавим этот список в список со списками
        List<InlineKeyboardButton> buttons2 = new ArrayList<>(); // затем добавим этот список в список со списками

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BUTTON"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("NO_BUTTON");






        buttons.add(yesButton);
        buttons.add(noButton);





        inlineRows.add(buttons); // добавляем список с кнопками в список списками

        markupInline.setKeyboard(inlineRows); // добавляем список со списком кнопок в tgобъект с кнопками

        message.setReplyMarkup(markupInline); // добавляем объект с кнопками в объект сообщения

        try {
            execute(message);
        } catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }
    @Override // что должен делать бот
    public void onUpdateReceived(Update update) { // Update класс содержит в себе сообщения которые пользователи посылают боту, так же инфу о пользователе

        /// если есть новое сообщение && есть текст в этом смс
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText(); /// сообщение от юзера лежит теперь здесь
            long chatId = update.getMessage().getChatId();
            // с пользователем могут общаться множество пользователей одновременно и боту надо знать чат айди с кем он общается и что окму отвечать

            // передаем параметром чат айди, getMessage() и из getChat достаем информацию о пользователе
//            if (update.equals("/start")) {
//                startCommandReceived(update.getMessage().getChatId());
//            } else {
//                sendMessage(chatId, "Прости, бро, команда пока не поддерживается");
//            }
            String firstName = update.getMessage().getChat().getFirstName(); ///
            switch (messageText){
                case "/start" :
//                    registerToUser(update.getMessage());
                    startCommandReceived(chatId, firstName);
                    break;
                case "/help" : helpCommandReceived(chatId, firstName);
                    break;
                case "/mydata" : myDataMethod(chatId, firstName);
                    break;
                case "/register" : register(chatId);
                    break;

            }

        }


        else if (update.hasCallbackQuery()){
            String callBackData = update.getCallbackQuery().getData(); // .getData() здесь содержится айди той кнопки на которую нажал пользователь (да\нет)

            long messageId = update.getCallbackQuery().getMessage().getMessageId(); // для того что бы изменять сообщение прямо в том же поля у пользователя, нам понадобится айди конкретного сообщения
            long chatId = update.getCallbackQuery().getMessage().getChatId();


            if (callBackData.equals("BUY_BUTTON")){
                String text = "Выберите криптовалюту для покупки: ";
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText(text);
                editMessageText.setMessageId((int) messageId);

                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>(); // создаем список со списками


                List<InlineKeyboardButton> buttons = new ArrayList<>(); // затем добавим этот список в список со списками
                List<InlineKeyboardButton> buttons_2 = new ArrayList<>(); // затем добавим этот список в список со списками
                List<InlineKeyboardButton> buttons_3 = new ArrayList<>(); // затем добавим этот список в список со списками


                // first row
                InlineKeyboardButton btc = new InlineKeyboardButton();
                btc.setText("Bitcoin");
                btc.setCallbackData("BITCOIN"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь
                // second
                InlineKeyboardButton usdt = new InlineKeyboardButton();
                usdt.setText("USDT");
                usdt.setCallbackData("USDT");
                // third
                InlineKeyboardButton monero = new InlineKeyboardButton();
                monero.setText("Monero");
                monero.setCallbackData("MONERO");

                buttons.add(btc);
                buttons_2.add(usdt);
                buttons_3.add(monero);


                inlineRows.add(buttons); // добавляем список с кнопками в список списками
                inlineRows.add(buttons_2); // добавляем список с кнопками в список списками
                inlineRows.add(buttons_3); // добавляем список с кнопками в список списками

                keyboardMarkup.setKeyboard(inlineRows); // добавляем список со списком кнопок в tgобъект с кнопками

                editMessageText.setReplyMarkup(keyboardMarkup);

            }



            if (callBackData.equals("YES_BUTTON")){
                String text = "You pressed YES button";
                EditMessageText editMessageText = new EditMessageText(); // создаем объект изменения сообщений
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText(text);
                editMessageText.setMessageId((int) messageId); // установим айди конкретного сообщения что бы изменить его

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(); // создаем tgобъект кнопок
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>(); // создаем список со списками
                List<InlineKeyboardButton> buttons = new ArrayList<>(); // затем добавим этот список в список со списками

                InlineKeyboardButton Family = new InlineKeyboardButton();
                Family.setText("Family");
                Family.setCallbackData("Family"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь

                InlineKeyboardButton Name = new InlineKeyboardButton();
                Name.setText("Name");
                Name.setCallbackData("Name");

                buttons.add(Family);
                buttons.add(Name);

                inlineRows.add(buttons); // добавляем список с кнопками в список списками

                markupInline.setKeyboard(inlineRows); // добавляем список со списком кнопок в tgобъект с кнопками

                editMessageText.setReplyMarkup(markupInline); // добавляем объект с кнопками в объект сообщения

                try {
                    execute(editMessageText);
                } catch (TelegramApiException exception){
                    log.error("Error occurred: " + exception.getMessage());
                }
            } else if (callBackData.equals("NO_BUTTON")){
                String text = "You pressed NO button";
                EditMessageText editMessageText = new EditMessageText(); // создаем объект изменения сообщений
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText(text);
                editMessageText.setMessageId((int) messageId); // установим айди конкретного сообщения что бы изменить его
                try {
                    execute(editMessageText);
                } catch (TelegramApiException exception){
                    log.error("Error occurred: " + exception.getMessage());
                }
            }
            if (callBackData.equals("BUY_BUTTON")){
                String text = "Выберите криптовалюту для покупки: ";
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(String.valueOf(chatId));
                editMessageText.setText(text);
                editMessageText.setMessageId((int) messageId);

                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>(); // создаем список со списками


                List<InlineKeyboardButton> buttons = new ArrayList<>(); // затем добавим этот список в список со списками
                List<InlineKeyboardButton> buttons_2 = new ArrayList<>(); // затем добавим этот список в список со списками
                List<InlineKeyboardButton> buttons_3 = new ArrayList<>(); // затем добавим этот список в список со списками


                // first row
                InlineKeyboardButton btc = new InlineKeyboardButton();
                btc.setText("Bitcoin");
                btc.setCallbackData("BITCOIN"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь
                // second
                InlineKeyboardButton usdt = new InlineKeyboardButton();
                usdt.setText("USDT");
                usdt.setCallbackData("USDT");
                // third
                InlineKeyboardButton monero = new InlineKeyboardButton();
                monero.setText("Monero");
                monero.setCallbackData("MONERO");

                buttons.add(btc);
                buttons_2.add(usdt);
                buttons_3.add(monero);


                inlineRows.add(buttons); // добавляем список с кнопками в список списками
                inlineRows.add(buttons_2); // добавляем список с кнопками в список списками
                inlineRows.add(buttons_3); // добавляем список с кнопками в список списками

                keyboardMarkup.setKeyboard(inlineRows); // добавляем список со списком кнопок в tgобъект с кнопками

                editMessageText.setReplyMarkup(keyboardMarkup);

                try {
                    execute(editMessageText);
                } catch (TelegramApiException exception){
                    log.error("Error occurred: " + exception.getMessage());
                }

            }

            if (callBackData.equals("BITCOIN")){
//                String text = "На какую сумму Вы хотите купить Bitcoin? \n\n" +
//                        "(Напишите сумму: от 0.00028 до 0.3 BTC)";
                // установим айди конкретного сообщения что бы изменить его


                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>(); // создаем список со списками


                List<InlineKeyboardButton> buttons = new ArrayList<>();

                InlineKeyboardButton priceCalculator = new InlineKeyboardButton();
                priceCalculator.setText("Калькулятор цен: ");
                priceCalculator.setCallbackData("VIEWPRICE"); // с помощью данного метода бот понимает на какую кнопку нажал пользователь

                buttons.add(priceCalculator);
                inlineRows.add(buttons); // добавляем список с кнопками в список списками
                keyboardMarkup.setKeyboard(inlineRows); // добавляем список со списком кнопок в tgобъект с кнопками


//                String text = null;
                //                    String[] btcPriceArr = btcPrice().split(",");
//                    String priceInt = btcPriceArr[1];
//                    String [] onlyPrice = priceInt.split("//")


                // Вывод текущего курса BTCRUB
//                System.out.println("Current BTCRUB price: " + lastPrice);

                String text = null;
                try {
                    text =  "Введите сумму которую хотите получить. От 0.0035 до 0.47 \n\n" +
                "Current BTCRUB price: " + btcPrice();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                EditMessageText editMessageText = new EditMessageText(); // создаем объект изменения сообщений
                    editMessageText.setChatId(String.valueOf(chatId));
                    editMessageText.setText(text);
                    editMessageText.setMessageId((int) messageId);
                    editMessageText.setReplyMarkup(keyboardMarkup);


                try {
                    execute(editMessageText);
                } catch (TelegramApiException exception){
                    log.error("Error occurred: " + exception.getMessage());
                }



            }

    }

    }
        private void sendMessage(long chatId, String textToSend){ // метод который отправляет сообщения пользователю параметром принимает айди  чата и то что нужно отправить

            SendMessage message = new SendMessage();  // создаем объект нового смс, вызываем его из встроенного телеграм пакета который мы установили в самом начале

            message.setChatId(String.valueOf(chatId));/// установим чат айди в объект нового сообщения что бы понимать с кем общаемся
            ///  String.valueOf(chatId) тип данных long chatId  необходимо преобразовать в тип данных стринг

            message.setText(textToSend); // установим в объект нового смс текст который нам нужно отправить
//
//            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//            List<KeyboardRow> keyboardRowList= new ArrayList<>(); // создаем список из рядов с кнопками
//            KeyboardRow keyboardRow = new KeyboardRow(); // KeyboardRow объект-ряд который содержит кнопки
//            keyboardRow.add("Погода");
//            keyboardRow.add("Анекдот");// две кнопки в один ряд
//            keyboardRowList.add(keyboardRow);
//
//            KeyboardRow keyboardRow1 = new KeyboardRow();
//            keyboardRow1.add("Register");
//            keyboardRow1.add("Check my data");
//            keyboardRow1.add("Delete my data");
//            keyboardRowList.add(keyboardRow1);
//
//            replyKeyboardMarkup.setKeyboard(keyboardRowList); // установили в объект клавиатуры наш список с кнопками
//            message.setReplyMarkup(replyKeyboardMarkup); // привязали к каждому сообщению



            try {
                execute(message); /// обработаем возможные исключения с помощью блока try catch // execute(message) --> пытаемся(execute) создать новый объект message
            } catch (TelegramApiException e){
                log.error("Error occurred: " + e.getMessage()); // в случае возникновения ошибки, записываем в логи соответствующую информацию
            }

        }

//        public void registerToUser(Message message){
//                if (userRepository.findById(message.getChatId()).isEmpty()){
//                    Long chatId = message.getChatId();
//                    Chat chat = message.getChat();
//
//                    User user = new User();
//                    user.setChatId(chatId);
//                    user.setFirstName(chat.getFirstName());
//                    user.setLastName(chat.getLastName());
//                    user.setUserName(chat.getUserName());
//                    user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
//
//                    userRepository.save(user);
//                    log.info("User saved "+ user);
//                }
//        }



public String btcPrice() throws IOException {

    OkHttpClient client = new OkHttpClient();

    // Создание запроса GET к API Binance для получения текущего курса пары BTCRUB
    Request request = new Request.Builder()
            .url("https://api.binance.com/api/v3/ticker/price?symbol=BTCRUB")
            .build();

    // Выполнение запроса
    Response response = client.newCall(request).execute();

    // Парсинг ответа
    String responseBody = response.body().string();
    JSONObject json = new JSONObject(responseBody);
    String lastPrice = json.getString("price");

    // Вывод текущего курса BTCRUB
    System.out.println("Current BTCRUB price: " + lastPrice );

    String[] arr = lastPrice.split("\\.");
    String price = arr[0];

    return price;

}

public double btcRubPrice() throws IOException {
    OkHttpClient client = new OkHttpClient();

    // Создание запроса GET к API Bybit для получения текущего курса биткойна
    Request request = new Request.Builder()
            .url("https://api.bybit.com/v2/public/tickers?symbol=BTCUSD")
            .build();

        // Выполнение запроса
        Response response = client.newCall(request).execute();

        // Парсинг ответа
        String responseBody = response.body().string();
        JSONObject json = new JSONObject(responseBody);
        double lastPrice = json.getJSONArray("result")
                .getJSONObject(0)
                .getDouble("last_price");

        // Вывод текущего курса биткойна
        System.out.println("Current Bitcoin price: $" + lastPrice);

        return lastPrice;

}

}