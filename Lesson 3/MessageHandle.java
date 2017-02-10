import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import com.pokegoapi.api.pokemon.Pokemon;

public class MessageHandle{
	static Update msg;
	static SendMessage message;
	static TelegramBot tgb;
	public static void MessageHandle(TelegramBot tgb, Update msg){
		MessageHandle.msg = msg;
		MessageHandle.tgb = tgb;
		if(msg.hasMessage()){
			if(msg.getMessage().hasText()){
				switch(msg.getMessage().getText().toUpperCase()){
					case "IV":
						showIV();
						break;
					case "GAMEID":
						showGameId();
						break;
				}
			}
		}
	}
	
	
	
	public static void showIV(){
		String str = "";
		for(int i=0;i<Example.go.size();i++){
			if(Example.go.get(i)!=null){
				str += "BOT[" + i + "] Game Id " + Example.go.get(i).getPlayerProfile().getPlayerData().getUsername() + "\n";
				for(int y=0 ; y < Example.go.get(i).getInventories().getPokebank().getPokemons().size() ; y++){
					Pokemon poke = Example.go.get(i).getInventories().getPokebank().getPokemons().get(y);
					str += "Pokemon : " + poke.getPokemonId();
					str += "[CP]" + poke.getCp();
					str += "[IV]" + poke.getIndividualAttack() + "/" + poke.getIndividualDefense() + "/" + poke.getIndividualStamina();
					str += "\n";
				}
			}
		}
		System.out.println(str);
		String[] breakDown = str.split("\n");
		int loopTime = (int)(breakDown.length / 10);
		int breakByLine = 10;
		for(int i=0; i<loopTime; i++ ){
			System.out.println(i);
			String tmp = "";
			for(int j=i*breakByLine; j<(i+1)*breakByLine; j++){
				System.out.println(j);
				try{
					tmp += breakDown[j] + "\n";
				}catch(Exception e){
					break;
				}
			}
			message = new SendMessage()
					.setChatId(msg.getMessage().getChatId())
					.setReplyToMessageId(msg.getMessage().getMessageId())
					.setText(tmp);
			try {
				tgb.sendMessage(message);
			} catch (Exception e) {
				System.out.println("Send out Error!" + e);
			}
		}	    
	}
	
	
	public static void showGameId(){
		String str = "";
		for(int i=0; i<Example.go.size();i++){
			str += "Bot[" + i + "] " + Example.go.get(i).getPlayerProfile().getPlayerData().getUsername() + "\n";
		}
		message = new SendMessage()
				.setChatId(msg.getMessage().getChatId())
				.setReplyToMessageId(msg.getMessage().getMessageId())
				.setText(str);
		try {
			tgb.sendMessage(message);
		} catch (Exception e) {
			System.out.println("Send out Error!" + e);
		}
	}
}