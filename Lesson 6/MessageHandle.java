import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.text.NumberFormat;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.gym.Gym;
import com.pokegoapi.api.map.Point;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.api.pokemon.Pokemon;

import POGOProtos.Data.PokemonDataOuterClass.PokemonData;

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
						case "STOP":
							Example.go.get(0).setLocation(22.2821181,114.1510632,1);
							stop();
							break;
					}
				}
			}
		}
		if(msg.getMessage().hasLocation()){
			findMon(Example.go.get(0),msg.getMessage().getLocation().getLatitude(),msg.getMessage().getLocation().getLongitude());
		}
	}
	
	
	
	private static void stop() {
		// TODO Auto-generated method stub
		PokemonGo api = Example.go.get(0);
		try {
			Collection<Pokestop> pokestops = api.getMap().getMapObjects().getPokestops();
			System.out.println("Found " + pokestops.size() + " pokestops in the current area.");

			Pokestop destinationPokestop = null;
			double des = 99999999;
			double tmp = 99999999;
			for (Pokestop pokestop : pokestops) {
				//Check if not in range and if it is not on cooldown
				if (!pokestop.inRange() && pokestop.canLoot(true)) {
					des = Math.abs(getDistance(22.2821181,114.1510632,pokestop.getLatitude(),pokestop.getLongitude()));
					if(destinationPokestop!=null)
						tmp = Math.abs(getDistance(22.2821181,114.1510632,destinationPokestop.getLatitude(),destinationPokestop.getLongitude()));
					if(des < tmp){
						//System.out.println("des : " + des + " tmp : "  + tmp);
						destinationPokestop = pokestop;
					}
				}
			}

			if (destinationPokestop != null) {
				Point destination = new Point(destinationPokestop.getLatitude(), destinationPokestop.getLongitude());
				//Use the current player position as the source and the pokestop position as the destination
				//Travel to Pokestop at 20KMPH	
				float speed=5;
				Path2 path = new Path2(api.getPoint(), destination, speed);
				System.out.println("Traveling to " + destination + " at " + speed + "KMPH!");
				path.start(api);
				try {
					message = new SendMessage()
							.setChatId(msg.getMessage().getChatId())
							.setReplyToMessageId(msg.getMessage().getMessageId())
							.setText("正在以時速[" + speed + "KM]前往目的地 剩余" + (path.getTimeLeft(api)/ 1000) + " 秒");
					tgb.sendMessage(message);
					while (!path.isComplete()) {
						Point point = path.calculateIntermediate(api);
						api.setLatitude(point.getLatitude());
						api.setLongitude(point.getLongitude());
						System.out.println("Time left: " + (path.getTimeLeft(api)/ 1000) + " seconds.");
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					return;
				}
				System.out.println("Finished traveling to pokestop!");
				if (destinationPokestop.inRange()) {
					System.out.println("Looting pokestop...");
					PokestopLootResult result = destinationPokestop.loot();
					System.out.println("Pokestop loot returned result: " + result.getResult());
					String str = "";
					switch(result.getResult().toString().replace(" ","")){
						case "SUCCESS":
							String item = "\n獲取道具:\n";
							for(int i=0;i<result.getItemsAwarded().size();i++){
								item += result.getItemsAwarded().get(i).getItemId() + "\n";
							}
							str = "轉動stop成功" + item + "\n獲取經驗" + result.getExperience();
							break;
						case "OUT_OF_RANGE" :
							str = "轉動stop失敗 原因: 超速駕駛 累己累人";
							break;
						default:
							str = "轉動stop失敗 原因:" + result.getResult().toString().replaceAll("_", " ");
					}
					
					
					if(str != ""){
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
				} else {
					System.out.println("Something went wrong! We're still not in range of the destination pokestop!");
				}
			} else {
				System.out.println("Couldn't find out of range pokestop to travel to!");
			}
		} catch (Exception e) {
			System.out.println("Error " + e);
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
			//go.getMap().awaitUpdate();
			Collection<CatchablePokemon> cp = go.getMap().getCatchablePokemon();
			System.out.println("Pokemon in area: " + cp.size());
			for (CatchablePokemon pm : cp) {
				EncounterResult encResult = pm.encounterPokemon();
				if(encResult.wasSuccessful()){
					System.out.println("找到精靈 " + pm.getPokemonId() );
					str += "坐標 : ";
					str += pm.getLatitude() + ",";
					str += pm.getLongitude();
					str += "\n";
					str += "精靈 : ";
					str += pm.getPokemonId();
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
			List<Gym> gym = go.getMap().getGyms();
			for (Gym g : gym) {				
				//List<GymMembership> gymObject = g.getGymMembers();
				str += "GYM : " + g.getLatitude() + "," + g.getLongitude() + "[" + g.getDescription() + "]"; 
				str += " \nColor: " + g.getOwnedByTeam() + "\n";
				List<PokemonData> gymStatus = g.getDefendingPokemon();
				for(PokemonData gymDetail : gymStatus){
					str += "訓練員 : <b>" + gymDetail.getOwnerName() + "</b>\n";
					str += "精靈名 : " + gymDetail.getPokemonId() + "[CP " + gymDetail.getCp() + "]";
					str += " [IV " + gymDetail.getIndividualAttack() + "/";
					str += gymDetail.getIndividualDefense() + "/";
					str += gymDetail.getIndividualStamina() + "]\n";
					str += "技能1  : " + gymDetail.getMove1() + "\n";
					str += "技能2  : " + gymDetail.getMove2() + "\n";
				}
				str += "\n";
			}
			message = new SendMessage()
					.setChatId(msg.getMessage().getChatId())
					.enableHtml(true)
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
	
	private static double EARTH_RADIUS = 6378.137;  
    
    private static double rad(double d) {  
        return d * Math.PI / 180.0;  
    }  
  

    public static double getDistance(double lat1, double lng1, double lat2,  
                                     double lng2) {  
        double radLat1 = rad(lat1);  
        double radLat2 = rad(lat2);  
        double a = radLat1 - radLat2;  
        double b = rad(lng1) - rad(lng2);  
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
                + Math.cos(radLat1) * Math.cos(radLat2)  
                * Math.pow(Math.sin(b / 2), 2)));  
        s = s * EARTH_RADIUS;  
        s = Math.round(s * 10000d) / 10000d;  
        s = s*1000;  
        return s;  
    }
	
	
	
}