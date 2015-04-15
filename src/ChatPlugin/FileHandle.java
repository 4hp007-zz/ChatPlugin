package ChatPlugin;

import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class FileHandle {
    
    File f;  
    FileReader fr;
    FileWriter fw;
    JSONParser parse;
    JSONObject jobj;
    FileHandle(String path){
        
        f = new File(path);
    }
    void insert(String ip,String data){
        
        try {
            fr = new FileReader(f);            
            parse = new JSONParser();
            jobj = (JSONObject)parse.parse(fr);
            jobj.put(ip, data);
            fr.close();
            fw = new FileWriter(f);
            fw.write(jobj.toString());            
            fw.close();
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFound");
        } catch (Exception ex) {
            System.out.println("JSON error");
        }
    }
    String read(String ip){
        
        try {
            fr = new FileReader(f);           
            parse = new JSONParser();
            jobj = (JSONObject)parse.parse(fr);
            fr.close();
            if(jobj.containsKey(ip))                
                return (String) jobj.get(ip);                                   
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFound");
        } catch (Exception ex) {
            System.out.println("JSON Read error");
        } 
        return "";
    }

}
