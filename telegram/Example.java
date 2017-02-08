import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Example{
  /*
   * 程序主入口 
   */
  public static void main(String args[]){
    /*
     * 創建一個新的telegramAPI 在內存裡面
     */
    TelegramBotsApi tgBot = new TelegramBotsApi();    
    /*
     * 初始化telegramAPI的所有函數
     */
    ApiContextInitializer.init();
    /*
     *try{}catch(Exception e) 是一組條件 代表嘗試 如果有程序錯誤 執行catch 如沒有 catch則不會執行
     */
        try {
          /*
           * 把創建出來的telegramAPI與機器人連接
           */
          tgBot.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
          System.out.println("error");
        }
  }
}
