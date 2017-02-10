import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class TelegramBot extends TelegramLongPollingBot {
	@Override

	public void onUpdateReceived(Update update) {
		System.out.println(update.getMessage());
		if(update.hasChannelPost()){
			
		}
		if(update.hasMessage()){
			MessageHandle.MessageHandle(this,update);
		}
	}
	@Override
	public String getBotUsername() {
		return "TEST-API";
	}
	@Override
	public String getBotToken() {
		return "<token>";
	}
}
