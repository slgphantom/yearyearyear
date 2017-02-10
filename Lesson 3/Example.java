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
			{"HX48761191","a6986601"}
	};
	/*
	 * �{��pokemon go api
	 */
	public static LinkedList<PokemonGo> go;
	
	
	/*
	 * ��������� 
	 */
	public static void main(String args[]) throws Exception{
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
				go.get(x).login(new PtcCredentialProvider(http, account[x][0],account[x][1]));				
    		}catch(Exception E1){
    			System.out.println("Login error 2 : " + E1);
    		}
		}
		
		
		
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