package ChatPlugin;


import java.nio.*;
import java.util.*;
import org.gudy.azureus2.plugins.messaging.*;


public class NewMessage implements Message{

    String text;    
    ByteBuffer b;
    
    
    public NewMessage(String text){
                
        this.text = text;
        generateBuffer();
    }
    @Override
    public String getID() {
       
        return "CHAT";
    }

    @Override
    public int getType() {
        
        return Message.TYPE_PROTOCOL_PAYLOAD;        
        
    }

    @Override
    public String getDescription() {
        
        return "";
        
    }

    @Override
    public ByteBuffer[] getPayload() {
        
        return new ByteBuffer[]{b};
    }
    
    private void generateBuffer() {
          
    Map<String,String> mMessage = new HashMap<>();   
    mMessage.put("t",text);
    
    byte[] bMessage = new byte[0];
    
    try {
      bMessage = Chat.bEncode(mMessage);      
    } catch(Exception exception) {      
    }    
    b = ByteBuffer.allocate( bMessage.length );
    b.put( bMessage );
    b.flip();
  }
  

    @Override
    public Message create(ByteBuffer data) throws MessageException {
        
         if( data == null ) {
      throw new MessageException( "decode error: data == null" );
    }
    
    
    
    int size = data.remaining();

    byte[] bMessage = new byte[ size ];
    data.get( bMessage );
    try {      
      Map mMessage = Chat.bDecode(bMessage);            
      String text1 = new String((byte[])mMessage.get("t"));
      return new NewMessage(text1);  
    } catch(Exception e) {
      throw new MessageException( "decode error" );
    }
    }

    @Override
    public void destroy() {
        
    }
    public String receive(){
         return text;
    }
    
}
