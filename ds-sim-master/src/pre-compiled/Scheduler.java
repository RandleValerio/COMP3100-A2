import java.io.*;
import java.net.*;

public class Scheduler{
public static void main(String args[]){
try{

    Socket s=new Socket("localhost",50000);

    DataOutputStream dout=new DataOutputStream(s.getOutputStream());
    BufferedReader in = new BufferedReader( new InputStreamReader( s.getInputStream()));

    //Handshake
    dout.write(("HELO"+"\n").getBytes());
    dout.flush();

    String reply=(String)in.readLine();

    String username = System.getProperty("user.name");
    dout.write(("AUTH "+username+"\n").getBytes());
    dout.flush();

    String reply2=in.readLine();

    while(true){
        //Send REDY
        dout.write(("REDY"+"\n").getBytes());
        dout.flush();

        //Read command from server (JOBN or JCPL or NONE)
        String reply3=in.readLine();
        String[] jobn=reply3.split(" ");

        //check if  server is finished sending jobs
        if(jobn[0].equals("NONE")){
            break;
        }

        //check if server is sending a job
        if(jobn[0].equals("JOBN")){

            //send GETS Command
            dout.write(("GETS Capable "+jobn[4]+" "+jobn[5]+" "+jobn[6]+"\n").getBytes());
            dout.flush();
            int reqCore = Integer.parseInt(jobn[4]);

            //read DATA
            String reply4=in.readLine();
            String[] servAmountArray=reply4.split(" ");
            int servAmountNum = Integer.parseInt(servAmountArray[1]);

            //send OK
            dout.write(("OK"+"\n").getBytes());
            dout.flush();

            String serverType=" ";
            String serverID =" ";

            //read SERVER INFO
            for(int i=0; i<servAmountNum; i++){
                String reply5=in.readLine();
                String[] serverArray=reply5.split(" ");
                int availCore = Integer.parseInt(serverArray[4]);
                if(i==0){
                    serverType=serverArray[0];
                    serverID=serverArray[1];
                }
            }

            //Send OK
            dout.write(("OK"+"\n").getBytes());
            dout.flush();

            //receive "."
            String reply6=in.readLine();


            //Send SCHD command (JOBID SERVERTYPE SERVER ID)
            dout.write(("SCHD "+jobn[2]+" "+serverType+" "+serverID+"\n").getBytes());
            dout.flush();

            //read OK
            String reply7=in.readLine();

        }// end of : if jobn[0].equals(“JOBN”)
    }//end of while loop

    //Send quit
    dout.write(("QUIT"+"\n").getBytes());
    dout.flush();

    //read quit
    String replyQuit=in.readLine();


    in.close();
    dout.close();
    s.close();
    }catch(Exception e){System.out.println(e);}
    }
}
