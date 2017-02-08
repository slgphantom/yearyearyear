import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsAPi;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class Example{
    public static void main(String args[]){
        TelegramBotsApi tgBot = new TelegramBotsApi();
        ApiContextInitializer.init();
        try {
            tgBot.registerBot(new TelegramBot());
        } catch (TelegramApiExpection e) {
            System.out.println("error");
        }
    }
}
