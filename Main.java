import java.io.*;
import java.net.*;
import java.util.*;

class Main{

	private final static String COOKIE=".ROBLOSECURITY=_|WARNING:-DO-NOT-SHARE-THIS.--Sharing-this-will-allow-someone-to-log-in-as-you-and-to-steal-your-ROBUX-and-items.|_?; .RBXID=_|WARNING:-DO-NOT-SHARE-THIS.--Sharing-this-will-allow-someone-to-log-in-as-you-and-to-steal-your-ROBUX-and-items.|_?;";

	public static void main(String args[]) throws Exception{

		System.out.println();

		String pid=args[0];
		 System.out.println("PlaceID: "+pid);

		String u=args[1];
		 System.out.println("Username: "+u);

		System.out.println();

		String uid  =getUserIDByUsername(u);
		 System.out.println("UserID: "+uid);

		String hsurl=getHSURLByUserID(uid);
		 System.out.println("HSURL: "+hsurl);

		System.out.println();

		String gids =getPlaceGameIDsByHSURL(pid,hsurl);
		 System.out.println("\nGameIDs:\n "+gids.replace("\n","\n "));

	}

	private static String getUserIDByUsername(String u) throws Exception{

		HttpURLConnection huc=(HttpURLConnection)new URL("https://www.roblox.com/users/profile?username="+u).openConnection();
		 huc.setUseCaches(false);
		 huc.setInstanceFollowRedirects(false);
		 huc.setRequestProperty("Cookie",COOKIE);

		String hfl=huc.getHeaderField("Location");

		huc.disconnect();

		return hfl.substring(7,hfl.length()-8);

	}

	private static String getHSURLByUserID(String uid) throws Exception{

		HttpURLConnection huc=(HttpURLConnection)new URL("https://www.roblox.com/headshot-thumbnail/image?userId="+uid+"&width=48&height=48&format=png").openConnection();
		 huc.setUseCaches(false);
		 huc.setInstanceFollowRedirects(false);
		 huc.setRequestProperty("Cookie",COOKIE);

		String hfl=huc.getHeaderField("Location");

		huc.disconnect();

		return hfl;

	}

	private static String getPlaceGameIDsByHSURL(String pid, String hsurl) throws Exception{

		String gids="";

		int si=0;
		int tcs=0;
		do{

			Thread[]  t_=new Thread[32]; // Limit 32
			String[] tds=new String[t_.length];

			for(int ti=0;ti<t_.length;ti++){

				final int tti=ti;
				final int tsi=si+ti*10;

				tds[ti]="";
				 t_[ti]=new Thread(){

					public void run(){

						try{

							HttpURLConnection huc=(HttpURLConnection)new URL("https://www.roblox.com/games/getgameinstancesjson?placeId="+pid+"&startIndex="+tsi).openConnection();
							 huc.setUseCaches(false);
							 huc.setInstanceFollowRedirects(false);
							 huc.setRequestProperty("Cookie",COOKIE);

							try(InputStream is=huc.getInputStream()){

								int dbr=-1;
								byte[] dbb_=new byte[4096];
								while((dbr=is.read(dbb_))>-1)tds[tti]+=new String(dbb_,0,dbr);

							}

							huc.disconnect();

						}catch(Exception e){

							e.printStackTrace();

						}

					}

				};

				t_[ti].start();

			}

			for(int ti=0;ti<t_.length;ti++){

				t_[ti].join();

				String ds=tds[ti];

				for(int dso=0;(dso=ds.indexOf(hsurl,dso))>-1;dso++){

					String gid=ds.substring(ds.indexOf("gameInstanceId=",dso)+15,ds.indexOf("gameInstanceId=",dso)+15+36);
					if(!gids.contains(gid))gids+=gid+"\n";

				}

				if(ds.contains("\"TotalCollectionSize\":")){

					tcs=Integer.valueOf(ds.substring(ds.indexOf("\"TotalCollectionSize\":")+22,ds.indexOf("}",ds.indexOf("\"TotalCollectionSize\":")+22)));

				}

				si+=10;

			}

			System.out.println(si+"/"+tcs+": "+gids.length()/37+" GIDs...");

		}while(si<tcs);

		return gids;

	}

}
