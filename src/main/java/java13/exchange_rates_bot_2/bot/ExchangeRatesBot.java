package java13.exchange_rates_bot_2.bot;

import java13.exchange_rates_bot_2.exception.ServiceException;
import java13.exchange_rates_bot_2.service.ExchangeRatesBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {

    private static final Logger LOG =  LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String HELP = "/help";

    @Autowired
    private ExchangeRatesBotService exchangeRatesBotService;

    public ExchangeRatesBot (@Value("${bot.token}") String botToken){
        super(botToken);
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()){
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId,userName);
            }
            case USD -> usdCommand(chatId);
            case EUR -> eurCommand(chatId);
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);

        }
    }

    private void helpCommand(Long chatId){
        var text = """
                Боттун фон маалыматы.
                
                Учурдагы алмашуу курстарын алуу үчүн буйруктарды колдонуңуз:
                /usd - доллардын курсу
                /eur - евронун курсу
                """;
        sendMessage(chatId,text);
    }

    @Override
    public String getBotUsername() {
        return "Temirbekov_corp_bot";
    }

    private void startCommand(Long chatId, String userName){
        var text = """
                Ботко кош келиңиз,%s
                ___________________________________
                Автору: Бектур Темирбеков
                       Маалыматтык технологиялар жана
                       программа боюнча адис.
                ___________________________________
                
                Бул жерден сиз Россия Федерациясынын Борбордук банкы тарабынан белгиленген
                бүгүнкү күнгө карата расмий курстары менен тааныша аласыз.
                
                Бул үчүн, буйруктарды колдонуңуз:
                
                /usd - доллардын курыу
                /eur - евронун курсу
                
                Кошумча буйруктар:
                /help жардам алуу
                
                """;
        var formattedText = String.format(text,userName);
        sendMessage(chatId,formattedText);
    }
    private void usdCommand(Long chatId){
        String formattedText;
        try {
            var usd = exchangeRatesBotService.getUSDExchangeRate();
            var text = "Доллардын курсу %s тузулушу %s рубль." +
                    " Доллар карап калыпсынго, Акчан кобоюп калдыбы?";
            formattedText = String.format(text, LocalDate.now(),usd);

        }catch (ServiceException e){
            LOG.error("Ошибка получения курса доллара.", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже";

        }
        sendMessage(chatId,formattedText);
    }

    private void eurCommand(Long chatId){
        String formattedText;
        try {
            var usd = exchangeRatesBotService.getEURExchangeRate();
            var text = "Евронун курсу %s тузулушу %s рубль";
            formattedText = String.format(text, LocalDate.now(),usd);

        }catch (ServiceException e){
            LOG.error("Ошибка получения курса евро.", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже";

        }
        sendMessage(chatId,formattedText);
    }

    private void unknownCommand(Long chatId){
        var text = "Эу нормальный команда жазсан! козун барбы?))";
        sendMessage(chatId,text);
    }

    private void sendMessage(Long chatId, String text){
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr,text);
        try {
            execute(sendMessage);

        }catch (TelegramApiException e){
            LOG.error("Билдирүү жөнөтүүдө ката кетти",e);
        }

    }
}
