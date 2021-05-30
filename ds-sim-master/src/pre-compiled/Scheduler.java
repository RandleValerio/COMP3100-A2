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

    String reply=in.readLine();

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

            //send GETS Avail
            dout.write(("GETS Avail "+jobn[4]+" "+jobn[5]+" "+jobn[6]+"\n").getBytes());
            dout.flush();
            int reqCore = Integer.parseInt(jobn[4]);

            //read DATA
            String reply4=in.readLine();
            String[] servAvailArray=reply4.split(" ");
            int servAmountNum = Integer.parseInt(servAvailArray[1]);

            //Declaring Variables
            String serverType=" ";
            String serverID=" ";
            int availCore=0;
            int fitValue=0;

            if(servAmountNum==0){
                //send OK
                dout.write(("OK"+"\n").getBytes());
                dout.flush();

                //receive ".'
                String dotReply = in.readLine();

                //send GETS Capable
                dout.write(("GETS Capable "+jobn[4]+" "+jobn[5]+" "+jobn[6]+"\n").getBytes());
                dout.flush();
                reqCore = Integer.parseInt(jobn[4]);

                //read DATA
                String reply8=in.readLine();
                String[] servCapArray=reply8.split(" ");
                servAmountNum = Integer.parseInt(servCapArray[1]);

                //send OK
                dout.write(("OK"+"\n").getBytes());
                dout.flush();

                //read SERVER INFO
                for(int i=0; i<servAmountNum; i++){
                    String reply9=in.readLine();
                    String[] serverArray=reply9.split(" ");
                    availCore = Integer.parseInt(serverArray[4]);
                    if(i==0){
                        serverType=serverArray[0];
                        serverID=serverArray[1];
                    }
                    if((availCore-reqCore)>=0){
                        fitValue=fitValue+1;
                    }
                    if(fitValue==1){
                        serverType=serverArray[0];
                        serverID=serverArray[1];
                    }
                }//end of GETS Capable for loop

                //Send OK
                dout.write(("OK"+"\n").getBytes());
                dout.flush();

                //receive "."
                String reply10=in.readLine();


                //Send SCHD command (JOBID SERVERTYPE SERVER ID)
                dout.write(("SCHD "+jobn[2]+" "+serverType+" "+serverID+"\n").getBytes());
                dout.flush();

                //read OK
                String reply11=in.readLine();
            }//end of if servAmount==0
            else{
                //send OK
                dout.write(("OK"+"\n").getBytes());
                dout.flush();

                for(int i=0;i<servAmountNum;i++){
                    String reply5=in.readLine();
                    String[] serverArray=reply5.split(" ");
                    availCore = Integer.parseInt(serverArray[4]);
                    if(i==0){
                        serverType=serverArray[0];
                        serverID=serverArray[1];
                    }
                    if((availCore-reqCore)>=1){
                        fitValue=fitValue+1;
                    }
                    if(fitValue==1){
                        serverType=serverArray[0];
                        serverID=serverArray[1];
                    }
                }

                //Send OK
                dout.write(("OK"+"\n").getBytes());
                dout.flush();

                //receive "."
                String reply6=in.readLine();

                //SCHD command (JOBID ServerType ServerID)
                dout.write(("SCHD "+jobn[2]+" "+serverType+" "+serverID+"\n").getBytes());
                dout.flush();

                //read OK
                String reply7=in.readLine();
            }//end of else statement
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
