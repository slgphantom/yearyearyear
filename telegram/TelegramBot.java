import org.telegrambots.api.methods.send.SendMessage;
import org.telegrambots.api.objects.Message;
import org.telegrambots.api.objects.Update;
import org.telegrambots.api.bots.TelegramLongPollingBot;

public class TelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage());
        try{
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                .sendChatId(update.getMessage().getChatId())
                .setReplyToMessageId(update.getMessage().getMessageId())
                .setText("Hello World!");
            this.sendMessage(message);
        }catch(Exception ex){
            System.out.println("Error");
        }
    }
    @Override
    public String getBotUsername() {
        return "Test-API";
    }
    @Override
    public String getBotToken() {
        return "<token>";
    }
}
