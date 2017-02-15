import java.util.LinkedList;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.PtcCredentialProvider;

import okhttp3.OkHttpClient;

public class Example{
	/*
	 * 定義二維賬號數組
	 */
	public static String[][] account = {
			{<id>}
	};
	/*
	 * 調用pokemon go api
	 */
	public static LinkedList<PokemonGo> go;
	
	public static String[] reply = 
		{
				"Do not speak",
				"不要说话",
				"不要說話",
		};
	
	/*
	 * 程序主入口 
	 */
	public static void main(String args[]){
		/*
         * ====================================================
         * pokemon go 登陸賬號部分
         * -暫時只用單線程登陸
         * ====================================================
         */
		go = new LinkedList<PokemonGo>();
        for(int x=0;x < account.length;x++){
        	OkHttpClient http = new OkHttpClient();
			go.add(new PokemonGo(http));
			try{
				System.out.println("check point 1");
				go.get(x).login(new PtcCredentialProvider(http, account[x][0],account[x][1]));	
				System.out.println("check point 2");
    		}catch(Exception E1){
    			System.out.println("Login error 2 : " + E1);
    			return;
    		}
		}
		System.out.println("login done."); 
		
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
        	System.out.println("error" + e);
        }      
        
        
	}
}