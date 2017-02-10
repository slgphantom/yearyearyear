import java.util.regex.Pattern;
import java.text.NumberFormat;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.api.pokemon.Pokemon;

public class MessageHandle{
	static Update msg;
	static SendMessage message;
	static TelegramBot tgb;
	public static void MessageHandle(TelegramBot tgb, Update msg){
		MessageHandle.msg = msg;
		MessageHandle.tgb = tgb;	
		if(msg.getMessage().getFrom().getId() == 123078226){
			if(msg.hasMessage()){
				if(msg.getMessage().hasText()){					
					String line =  msg.getMessage().getText().replace(" ", "");
					Pattern r = Pattern.compile("\\-*[0-9]+\\.[0-9]+,\\-*[0-9]+\\.[0-9]+");
					if (r.matcher(line).find()) {
						String[] pinfo = line.split(",");
						try {
							double x,y;
							NumberFormat df = NumberFormat.getNumberInstance();
							df.setMaximumFractionDigits(6);
							x = Float.parseFloat(df.format(Float.parseFloat(pinfo[0])));
							y = Float.parseFloat(df.format(Float.parseFloat(pinfo[1])));
							findMon(Example.go.get(0),x,y);
						}catch(Exception ex){}
					}
					
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
		if(msg.getMessage().hasLocation()){
			findMon(Example.go.get(0),msg.getMessage().getLocation().getLatitude(),msg.getMessage().getLocation().getLongitude());
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
		for(int i=0; i<=loopTime; i++ ){
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
	
	public static void findMon(PokemonGo go, double x, double y){
		go.setLocation(x, y, 1);
		System.out.println("飛往坐標地點 " + "[" + x + "，" + y + "]");
		String str = "";
		try {
			for(int i=0; i < go.getMap().getCatchablePokemon().size(); i++){
				EncounterResult encResult = go.getMap().getCatchablePokemon().get(i).encounterPokemon();
				if(encResult.wasSuccessful()){
					System.out.println("找到精靈 " + go.getMap().getCatchablePokemon().get(i).getPokemonId() );
					str += "坐標 : ";
					str += go.getMap().getCatchablePokemon().get(i).getLatitude() + ",";
					str += go.getMap().getCatchablePokemon().get(i).getLongitude();
					str += "\n";
					str += "精靈 : ";
					str += go.getMap().getCatchablePokemon().get(i).getPokemonId();
					str += " [CP : " + encResult.getPokemonData().getCp() + "]";
					str += "\n";
					str += "三圍 : " ;
					str += encResult.getPokemonData().getIndividualAttack() + " / ";
					str += encResult.getPokemonData().getIndividualDefense() + " / ";
					str += encResult.getPokemonData().getIndividualStamina();
					str += "\n";
					str += "技能1 : " + encResult.getPokemonData().getMove1();
					str += "\n";
					str += "技能2 : " + encResult.getPokemonData().getMove2();
					str += "\n\n";
				}
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
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}		
	}	
}