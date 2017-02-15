import java.util.LinkedList;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.PtcCredentialProvider;

import okhttp3.OkHttpClient;

public class Example{
	/*
	 * ���x���S�~̖���M
	 */
	public static String[][] account = {
			{<id>}
	};
	/*
	 * �{��pokemon go api
	 */
	public static LinkedList<PokemonGo> go;
	
	public static String[] reply = 
		{
				"Do not speak",
				"��Ҫ˵��",
				"��Ҫ�fԒ",
		};
	
	/*
	 * ��������� 
	 */
	public static void main(String args[]){
		/*
         * ====================================================
         * pokemon go ����~̖����
         * -���rֻ�Æξ��̵��
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
		 * ����һ���µ�telegramAPI �ڃȴ��e��
		 */
		TelegramBotsApi tgBot = new TelegramBotsApi();		
		/*
		 * ��ʼ��telegramAPI�����к���
		 */
		ApiContextInitializer.init();
		/*
		 *try{}catch(Exception e) ��һ�M�l�� ����Lԇ ����г����e�` ����catch ��]�� catch�t��������
		 */
        try {
        	/*
        	 * �ф��������telegramAPI�c�C�����B��
        	 */
        	tgBot.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
        	System.out.println("error" + e);
        }      
        
        
	}
}