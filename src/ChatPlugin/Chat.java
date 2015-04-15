package ChatPlugin;

import java.io.*;
import java.util.*;
import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.plugins.messaging.*;
import org.gudy.azureus2.plugins.network.*;
import org.gudy.azureus2.plugins.peers.*;
import org.gudy.azureus2.plugins.ui.*;
import org.gudy.azureus2.plugins.ui.menus.*;
import org.gudy.azureus2.plugins.ui.menus.MenuItem;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.plugins.utils.*;
import org.gudy.azureus2.ui.swt.plugins.*;

public class Chat implements Plugin {

    ArrayList<Peer> block = new ArrayList<>();
    HashMap<Peer, ChatWindow> hm = new HashMap<>();                      
    FileHandle f;
    UISWTInstance swtui;
    private static Formatters formatters = null;

    @Override
    public void initialize(final PluginInterface pi) throws PluginException {
                
        pi.getUIManager().addUIListener(new UIManagerListener() {

            @Override
            public void UIAttached(UIInstance instance) {

                if (instance instanceof UISWTInstance) {
                    swtui = (UISWTInstance) instance;
                }
            }         
            @Override
            public void UIDetached(UIInstance instance) {
            }
        });
        f = new FileHandle("chats.json");
        
        
        
        formatters = pi.getUtilities().getFormatters();
        TableContextMenuItem tm = pi.getUIManager().getTableManager().addContextMenuItem(TableManager.TABLE_TORRENT_PEERS, "New Chat");              
        try {
            pi.getMessageManager().registerMessageType(new NewMessage(""));          
        } catch (MessageException ex) {

            System.err.println("peer error");            

        }

        pi.getMessageManager().locateCompatiblePeers(pi, new NewMessage(""), new MessageManagerListener() {

            @Override
            public void compatiblePeerFound(Download download, final Peer peer, Message message) {

                peer.getConnection().getIncomingMessageQueue().registerListener(new IncomingMessageQueueListener() {

                    @Override
                    public boolean messageReceived(Message message) {
                        if (message.getID().equals("CHAT")) {
                            if (block.contains(peer))
                                return true;                                                                                                                       
                            final NewMessage nm = (NewMessage) message;                            
                            if (hm.containsKey(peer)) {
                                if (hm.get(peer).isDisposed()) {
                                    
                                    UIInstance ui = (UIInstance)swtui;                                
                                String[] op = {"Chat","Block"};
                                int l =ui.promptUser("New Chat", "Chat Invitation from "+peer.getIp(), op , 0);                                                               
                                if (l == 1) {
                                    block.add(peer);
                                    peer.getConnection().getOutgoingMessageQueue().sendMessage(new NewMessage("code:21215311")); 
                                    return true;
                                }
                                else if(l==-1){
                                    return true;
                                }

                                    swtui.getDisplay().asyncExec(new Runnable() {

                                        @Override
                                        public void run() {

                                            hm.get(peer).run(swtui.createShell(1), nm.receive());
                                            
                                        }

                                    });
                                } else {
                                    hm.get(peer).update(nm.receive());

                                }
                            } else {
                                hm.put(peer, new ChatWindow(peer,f));
                                UIInstance ui = (UIInstance)swtui;                                
                                String[] op = {"Chat","Block"};
                                int l =ui.promptUser("New Chat", "Chat Invitation from "+peer.getIp(), op , 0);                                                               
                                if (l == 1) {
                                    block.add(peer);
                                    peer.getConnection().getOutgoingMessageQueue().sendMessage(new NewMessage("code:21215311")); 
                                    return true;
                                }
                                else if(l==-1){
                                    return true;
                                }
                                swtui.getDisplay().asyncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        hm.get(peer).run(swtui.createShell(1), nm.receive());
                                    }
                                });
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void bytesReceived(int byte_count) {
                    }
                });

            }

            @Override
            public void peerRemoved(Download download, Peer peer) {

            }
        });
        tm.addListener(new MenuItemListener() {

            @Override
            public void selected(MenuItem menu, Object target) {

                TableRow rw = (TableRow) target;
                final Peer peer = (Peer) rw.getDataSource();                
                if (hm.containsKey(peer)) {
                    if (hm.get(peer).isDisposed()) {
                        swtui.getDisplay().asyncExec(new Runnable() {

                            @Override
                            public void run() {

                                hm.get(peer).run(swtui.createShell(1), null);

                            }

                        });
                    }
                    hm.get(peer).shell.forceActive();
                } else {

                    hm.put(peer, new ChatWindow(peer,f));
                    swtui.getDisplay().asyncExec(new Runnable() {

                        @Override
                        public void run() {

                            hm.get(peer).run(swtui.createShell(1), null);

                        }

                    });
                }
            }

        });

    }

    public static byte[] bEncode(Map map) {
        if (formatters == null) {
            return new byte[0];
        }
        try {
            return formatters.bEncode(map);
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static Map bDecode(byte[] bytes) {
        if (formatters == null) {
            return new HashMap();
        }
        try {
            return formatters.bDecode(bytes);
        } catch (IOException e) {
            return new HashMap();
        }
    }

}
