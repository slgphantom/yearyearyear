import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.listener.LoginListener;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.util.CaptchaSolveHelper;
import com.pokegoapi.util.Log;
import com.sun.javafx.application.PlatformImpl;
import com.twocaptcha.api.TwoCaptchaService;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import okhttp3.OkHttpClient;

public class Example{

	public static boolean use2captcha = false; // use 2captcha or not
	public static String url = "";
	public static String[][] account = {
			<account>
	};
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
        	url = "";
        	OkHttpClient http = new OkHttpClient();
			go.add(new PokemonGo(http));
			try{
				go.get(x).addListener(new LoginListener() {
					@Override
					public void onLogin(PokemonGo api) {
						System.out.println("Successfully logged in with SolveCaptchaExample!");
					}

					@Override
					public void onChallenge(PokemonGo api, String challengeURL) {
						System.out.println("Captcha received! URL: " + challengeURL);
						url = challengeURL;
					}
				});
				go.get(x).login(new PtcCredentialProvider(http, account[x][0],account[x][1]));
				if(url != ""){
					if(use2captcha){
						use2captcha(go.get(x));
					}else{
						completeCaptcha(go.get(x),url);
					}
				}
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




	private static void use2captcha(PokemonGo api) {
		System.out.println(url);
		String apiKey = <2captcha api key>;
		String googleKey = "6LeeTScTAAAAADqvhqVMhPpr_vB9D364Ia-1dSgK";
		String pageUrl = url;
		TwoCaptchaService service = new TwoCaptchaService(apiKey, googleKey, pageUrl);
		try {
			String responseToken = service.solveCaptcha();
			System.out.println("The response token is: " + responseToken);
			if (api.verifyChallenge(responseToken)) {
				System.out.println("Captcha was correctly solved!");
			} else {
				System.out.println("Captcha was incorrectly solved! Please try again.");
			}
		} catch (Exception e) {
			System.out.println("ERROR case 1");
			e.printStackTrace();
		}
	}




	private static void completeCaptcha(final PokemonGo api, final String challengeURL) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				PlatformImpl.startup(new Runnable() {
					@Override
					public void run() {
					}
				});

				Platform.runLater(new Runnable() {
					public void run() {
						JFXPanel panel = new JFXPanel();
						WebView view = new WebView();
						WebEngine engine = view.getEngine();
						engine.setUserAgent(CaptchaSolveHelper.USER_AGENT);
						engine.load(challengeURL);
						final JFrame frame = new JFrame("Solve Captcha");
						CaptchaSolveHelper.Listener listener = new CaptchaSolveHelper.Listener() {
							public void onTokenReceived(String token) {
								System.out.println("Token received: " + token + "!");
								CaptchaSolveHelper.removeListener(this);
								try {
									frame.setVisible(false);
									frame.dispose();

									if (api.verifyChallenge(token)) {
										System.out.println("Captcha was correctly solved!");
									} else {
										System.out.println("Captcha was incorrectly solved! Please try again.");
										//api.checkChallenge();
									}
								} catch (Exception e) {
									Log.e("Main", "Error while solving captcha!", e);
								}
							}
						};
						CaptchaSolveHelper.registerListener(listener);
						panel.setScene(new Scene(view));
						frame.getContentPane().add(panel);
						frame.setSize(500, 500);
						frame.setVisible(true);
						//Don't allow this window to be closed
						frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
						frame.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(WindowEvent e) {
								System.out.println("Please solve the captcha before closing the window!");
							}
						});
					}
				});
			}
		});
	}
	
	
}