import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class TelegramBot extends TelegramLongPollingBot {
  @Override
  /*
   * 接收訊息時 運行此內容
   */
  public void onUpdateReceived(Update update) {
    /*
     * 在命令欄顯示接收的消息 json格式
     */
    System.out.println(update.getMessage());
    try{
      String str = "";

      for(int i=0;i<Example.go.size();i++){
        if(Example.go.get(i)!=null){
          str += "BOT[" + i + "] Game Id " + Example.go.get(i).getPlayerProfile().getPlayerData().getUsername() + "\n";
        }
      }
      /*
       * 設定發消息內容 封裝為一個新封包
       */
      SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
              .setChatId(update.getMessage().getChatId())
              .setReplyToMessageId(update.getMessage().getMessageId())
              .setText(str);        
          /*
       * 調用super class的sendMessage功能 把封包發出去
       */
      this.sendMessage(message);
    }catch(Exception ex){
      System.out.println("Error");
    }
  }
  @Override
  /*
   * 給機器人一個名字
   */
  public String getBotUsername() {
    return "TEST-API";
  }
  @Override
  /*
   * 讓API知道哪個機器人需要運行
   */
  public String getBotToken() {
    return "<token>";
  }
}
