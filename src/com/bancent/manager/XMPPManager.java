package com.bancent.manager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.bancent.R;
import com.bancent.common.RetCode;
import com.bancent.common.RetCode.LoginResult;
import com.bancent.common.RetCode.RegistResult;
import com.bancent.common.TraceLog;
import com.bancent.common.Utils;
import com.bancent.common.XMPPConfig;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class XMPPManager
{
    private Context mContext = null;
    private static XMPPManager mInstance = null;
    private ConnectionConfiguration mConfig = null;
    private XMPPConnection mConnection = null;
    private XMPPConfig mXmppCfg = null;
    
    private XMPPManager()
    {}

    public static XMPPManager GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new XMPPManager();
        }
        return mInstance;
    }
    
    public void Init(Context ctx)
    {
        mContext = ctx;
    }
    
    public void InitBeforeLogin(XMPPConfig cfg)
    {
        mXmppCfg = cfg;
        if (mConfig == null)
        {
//            String host_ip = Utils.GetIPFromHost(cfg.GetHostIP());
            String host_ip = mContext.getResources().getString(R.string.default_xmpp_host_name);
            TraceLog.Print_I("InitBeforeLogin: host: " + host_ip);
            
            if (host_ip == null)
            {
                //notify to ui
                return;
            }
            
            mConfig = new ConnectionConfiguration(host_ip, cfg.GetHostPort(), cfg.GetServiceName());
            mConfig.setSASLAuthenticationEnabled(false);// 不使用SASL验证，设置为false
            mConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
            // 允许自动连接
            mConfig.setReconnectionAllowed(true);
            // 允许登陆成功后更新在线状态
            mConfig.setSendPresence(true);
            // 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
        }
        
        if (mConnection == null)
        {
            mConnection = new XMPPConnection(mConfig);
        }
    }
    
    public XMPPConnection GetConnection()
    {
        return mConnection;
    }
    /** 
     * 打开连接 
     */ 
    public boolean OpenConnection()
    {  
        try
        {  
            if (null == mConnection || !mConnection.isAuthenticated())
            {  
                XMPPConnection.DEBUG_ENABLED = true;// 开启DEBUG模式  
                // 配置连接  
//                ConnectionConfiguration config = new ConnectionConfiguration(  
//                        SERVER_HOST, SERVER_PORT, SERVER_NAME);  
//                config.setReconnectionAllowed(true);  
//                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);  
//                config.setSendPresence(true); // 状态设为离线，目的为了取离线消息  
//                config.setSASLAuthenticationEnabled(false); // 是否启用安全验证  
//                config.setTruststorePath("/system/etc/security/cacerts.bks");  
//                config.setTruststorePassword("changeit");  
//                config.setTruststoreType("bks");  
//                mConnection = new XMPPConnection(config);  
                mConnection.connect();// 连接到服务器  
                // 配置各种Provider，如果不配置，则会无法解析数据  
                configureConnection(ProviderManager.getInstance());  
                return true;  
            }  
        }
        catch (XMPPException xe)
        {  
            xe.printStackTrace();  
            mConnection = null;  
        }  
        return false;  
    }  
   
    /** 
     * 关闭连接 
     */ 
    public void CloseConnection() {  
        if(mConnection!=null){  
            //移除連接監聽  
            //connection.removeConnectionListener(connectionListener);  
            if(mConnection.isConnected())  
                mConnection.disconnect();  
            mConnection = null;  
        }  
        TraceLog.Print_D("XMPPManager: close connection");
    }  

    /** 
     * 登录 
     *  
     * @return 
     */ 
    public int login()
    {
        String name = mXmppCfg.GetLoginName();
        String pwd = mXmppCfg.GetLoginPWD();
        return login(name, pwd);
    }
    
    /** 
     * 登录 
     *  
     * @param account 
     *            登录帐号 
     * @param password 
     *            登录密码 
     * @return 
     */ 
    public int login(String account, String password)
    {  
        int ret = RetCode.RC_FAILED;
        try
        {  
            if (mConnection == null)  
                return ret;  
            
            if (!mConnection.isConnected())
            {
                OpenConnection();
            }
            
            mConnection.login(account, password);  
            // 更改在綫狀態  
            Presence presence = new Presence(Presence.Type.available);  
            mConnection.sendPacket(presence);  
            // 添加連接監聽  
//            connectionListener = new TaxiConnectionListener();  
//            mConnection.addConnectionListener(connectionListener);  
            ret = RetCode.RC_OK;
            return ret;  
        }
        catch (Exception e)
        {  
            TraceLog.Print_E("XMPPManager: login failed.");
            e.printStackTrace();
            ret = RetCode.RC_FAILED;
            
            if (e instanceof XMPPException)
            {
                XMPPException xe = (XMPPException) e;
                final XMPPError error = xe.getXMPPError();
                
                if (error != null)
                {
                    int errorCode = error.getCode();

                    if (errorCode == 401)
                    {
                        ret = LoginResult.RET_ERROR_ACCOUNT_PWD;
                    }
                    else if (errorCode == 403)
                    {
                        ret = LoginResult.RET_ERROR_ACCOUNT_PWD;
                    } 
                    else
                    {
                        ret = LoginResult.RET_ERROR_SERVER_NR;
                    }
                }
            }
        }  
        return ret;  
    }  
   
    /** 
     * 注册 
     *  
     * @param account 
     *            注册帐号 
     * @param password 
     *            注册密码 
     * @return 0x200=成功，
     *         0x201=服务器没有返回结果, 
     *         0x202=这个账号已经存在,
     *         0x203=注册失败 (未知错误)
     */ 
    public int RegistUser(String account, String password)
    {  
        if (mConnection == null)  
            return RetCode.RC_FAILED;  
        
        Registration reg = new Registration();  
        reg.setType(IQ.Type.SET);  
        reg.setTo(mConnection.getServiceName());  
        
        // 注意这里createAccount注册时，参数是UserName，不是jid，是"@"前面的部分。  
        reg.setUsername(account);  
        reg.setPassword(password);  
        // 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！  
        reg.addAttribute("android", "geolo_createUser_android");  
        PacketFilter filter = new AndFilter(new PacketIDFilter(  
                reg.getPacketID()), new PacketTypeFilter(IQ.class));  
        PacketCollector collector = mConnection.createPacketCollector(  
                filter);  
        mConnection.sendPacket(reg);  
        IQ result = (IQ) collector.nextResult(SmackConfiguration  
                .getPacketReplyTimeout());  
        // Stop queuing results停止请求results（是否成功的结果）  
        collector.cancel();  
        
        if (result == null)
        {  
            TraceLog.Print_E("No response from server.");  
            return RegistResult.RET_ERROR_SERVER_NR;  
        }
        else if (result.getType() == IQ.Type.RESULT)
        {  
            TraceLog.Print_D("regist success.");  
            return RetCode.RC_OK;  
        }
        else
        { // if (result.getType() == IQ.Type.ERROR)  
            if (result.getError().toString().equalsIgnoreCase("conflict(409)"))
            {  
                TraceLog.Print_E("IQ.Type.ERROR: " 
                        + result.getError().toString());  
                return RegistResult.RET_ERROR_ACCOUNT_EXIST;  
            }
            else
            {  
                TraceLog.Print_E("IQ.Type.ERROR: " 
                        + result.getError().toString());  
                return RetCode.RC_FAILED;  
            }  
        }  
    }  
   
    /** 
     * 更改用户状态 
     */ 
    public void setPresence(int code)
    {  
        if (mConnection == null)  
            return;  
        Presence presence = null;  
        switch (code)
        {  
        case 0:  
            presence = new Presence(Presence.Type.available);  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set online");  
            break;  
        case 1:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.chat);  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set Q me");  
            break;  
        case 2:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.dnd);  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set busy");  
            break;  
        case 3:  
            presence = new Presence(Presence.Type.available);  
            presence.setMode(Presence.Mode.away);  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set leave");  
            break;  
        case 4:  
            Roster roster = mConnection.getRoster();  
            Collection<RosterEntry> entries = roster.getEntries();  
            for (RosterEntry entry : entries)
            {  
                presence = new Presence(Presence.Type.unavailable);  
                presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
                presence.setFrom(mConnection.getUser());  
                presence.setTo(entry.getUser());  
                mConnection.sendPacket(presence);  
                TraceLog.Print_D("state: " + presence.toXML());  
            }  
            
            // 向同一用户的其他客户端发送隐身状态  
            presence = new Presence(Presence.Type.unavailable);  
            presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
            presence.setFrom(mConnection.getUser());  
            presence.setTo(StringUtils.parseBareAddress(mConnection.getUser()));  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set not available");  
            break;  
        case 5:  
            presence = new Presence(Presence.Type.unavailable);  
            mConnection.sendPacket(presence);  
            TraceLog.Print_D("set offline");  
            break;  
        default:  
            break;  
        }  
    }  
   
    /** 
     * 获取所有组 
     *  
     * @return 所有组集合 
     */ 
    public List<RosterGroup> getGroups()
    {  
        if (mConnection == null)  
            return null;  
        
        List<RosterGroup> grouplist = new ArrayList<RosterGroup>();  
        Collection<RosterGroup> rosterGroup = mConnection.getRoster()  
                .getGroups();  
        Iterator<RosterGroup> i = rosterGroup.iterator(); 
        
        while (i.hasNext()) 
        {  
            grouplist.add(i.next());  
        }  
        return grouplist;  
    }  
   
    /** 
     * 获取某个组里面的所有好友 
     *  
     * @param roster 
     * @param groupName 
     *            组名 
     * @return 
     */ 
    public List<RosterEntry> getEntriesByGroup(String groupName)
    {  
        if (mConnection == null)  
            return null;  
        
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();  
        RosterGroup rosterGroup = mConnection.getRoster().getGroup(  
                groupName);  
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        
        while (i.hasNext()) 
        {  
            Entrieslist.add(i.next());  
        }  
        return Entrieslist;  
    }  
   
    /** 
     * 获取所有好友信息 
     *  
     * @return 
     */ 
    public List<RosterEntry> getAllEntries()
    {  
        if (mConnection == null)  
            return null;  
        
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();  
        Collection<RosterEntry> rosterEntry = mConnection.getRoster()  
                .getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        
        while (i.hasNext())
        {  
            Entrieslist.add(i.next());  
        }  
        return Entrieslist;  
    }  
   
    /** 
     * 获取用户VCard信息 
     *  
     * @param connection 
     * @param user 
     * @return 
     * @throws XMPPException 
     */ 
    public VCard getUserVCard(String user)
    {  
        if (mConnection == null)  
            return null;  
        
        VCard vcard = new VCard();  
        try
        {  
            vcard.load(mConnection, user);  
        }
        catch (XMPPException e)
        {  
            e.printStackTrace();  
        }  
        return vcard;  
    }  
   
    /** 
     * 获取用户头像信息 
     *  
     * @param connection 
     * @param user 
     * @return 
     */ 
    public Drawable getUserImage(String user)
    {  
        if (mConnection == null)  
            return null;  
        
        ByteArrayInputStream bais = null; 
        
        try
        {  
            VCard vcard = new VCard();  
            // 加入这句代码，解决No VCard for  
            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp",  
                    new org.jivesoftware.smackx.provider.VCardProvider());  
            if (user == "" || user == null || user.trim().length() <= 0)
            {  
                return null;  
            }  
            
            vcard.load(mConnection, user + "@" 
                    + mConnection.getServiceName());  
   
            if (vcard == null || vcard.getAvatar() == null)  
                return null;  
            
            bais = new ByteArrayInputStream(vcard.getAvatar());  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
            return null;  
        }  
        return Utils.InputStream2Drawable(bais);  
    }  
   
    /** 
     * 添加一个分组 
     *  
     * @param groupName 
     * @return 
     */ 
    public boolean addGroup(String groupName)
    {  
        if (mConnection == null)  
            return false;  
        
        try
        {  
            mConnection.getRoster().createGroup(groupName);  
            TraceLog.Print_I("addGroup: " + groupName + "successful.");  
            return true;  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
            return false;  
        }  
    }  
   
    /** 
     * 删除分组 
     *  
     * @param groupName 
     * @return 
     */ 
    public boolean removeGroup(String groupName)
    {  
        return true;  
    }  
   
    /** 
     * 添加好友 无分组 
     *  
     * @param userName 
     * @param name 
     * @return 
     */ 
    public boolean addUser(String userName, String name)
    {  
        if (mConnection == null)  
            return false;
        
        try 
        {  
            mConnection.getRoster().createEntry(userName, name, null);  
            return true;  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
            return false;  
        }  
    }  
   
    /** 
     * 添加好友 有分组 
     *  
     * @param userName 
     * @param name 
     * @param groupName 
     * @return 
     */ 
    public boolean addUser(String userName, String name, String groupName)
    {  
        if (mConnection == null)  
            return false;  
        
        try
        {  
            Presence subscription = new Presence(Presence.Type.subscribed);  
            subscription.setTo(userName);  
            userName += "@" + mConnection.getServiceName();  
            mConnection.sendPacket(subscription);  
            mConnection.getRoster().createEntry(userName, name,  
                    new String[] { groupName });  
            return true;  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
            return false;  
        }  
    }  
   
    /** 
     * 删除好友 
     *  
     * @param userName 
     * @return 
     */ 
    public boolean removeUser(String userName)
    {  
        if (mConnection == null)  
            return false;  
        
        try
        {  
            RosterEntry entry = null;  
            if (userName.contains("@"))  
                entry = mConnection.getRoster().getEntry(userName);  
            else 
                entry = mConnection.getRoster().getEntry(  
                        userName + "@" + mConnection.getServiceName());  
            if (entry == null)  
                entry = mConnection.getRoster().getEntry(userName);  
            mConnection.getRoster().removeEntry(entry);  
   
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
   
    /** 
     * 查询用户 
     *  
     * @param userName 
     * @return 
     * @throws XMPPException 
     */ 
    public List<HashMap<String, String>> searchUsers(String userName)
    {  
        if (mConnection == null)  
            return null;  
        
        HashMap<String, String> user = null;  
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();  
        
        try
        {  
            new ServiceDiscoveryManager(mConnection);  
   
            UserSearchManager usm = new UserSearchManager(mConnection);  
   
            Form searchForm = usm.getSearchForm(mConnection  
                    .getServiceName());  
            Form answerForm = searchForm.createAnswerForm();  
            answerForm.setAnswer("userAccount", true);  
            answerForm.setAnswer("userPhote", userName);  
            ReportedData data = usm.getSearchResults(answerForm, "search" 
                    + mConnection.getServiceName());  
   
            Iterator<Row> it = data.getRows();  
            Row row = null;  
            
            while (it.hasNext())
            {  
                user = new HashMap<String, String>();  
                row = it.next();  
                user.put("userAccount", row.getValues("userAccount").next()  
                        .toString());  
                user.put("userPhote", row.getValues("userPhote").next()  
                        .toString());  
                results.add(user);  
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空  
            }  
        }
        catch (XMPPException e)
        {  
            e.printStackTrace();  
        }  
        return results;  
    }  
   
    /** 
     * 修改心情 
     *  
     * @param connection 
     * @param status 
     */ 
    public void changeStateMessage(String status)
    {  
        if (mConnection == null)  
            return;  
        
        Presence presence = new Presence(Presence.Type.available);  
        presence.setStatus(status);  
        mConnection.sendPacket(presence);  
    }  
   
    /** 
     * 修改用户头像 
     *  
     * @param file 
     */ 
    public boolean changeImage(File file)
    {  
        if (mConnection == null)  
            return false;  
        try
        {  
            VCard vcard = new VCard();  
            vcard.load(mConnection);  
   
            byte[] bytes;  
   
            bytes = getFileBytes(file);  
            String encodedImage = StringUtils.encodeBase64(bytes);  
            vcard.setAvatar(bytes, encodedImage);  
            vcard.setEncodedImage(encodedImage);  
            vcard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" 
                    + encodedImage + "</BINVAL>", true);  
   
            ByteArrayInputStream bais = new ByteArrayInputStream(  
                    vcard.getAvatar());  
            Utils.InputStream2Bitmap(bais);  
   
            vcard.save(mConnection);  
            return true;  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
            return false;  
        }  
    }  
   
    /** 
     * 文件转字节 
     *  
     * @param file 
     * @return 
     * @throws IOException 
     */ 
    private byte[] getFileBytes(File file) throws IOException
    {  
        BufferedInputStream bis = null;  
        try
        {  
            bis = new BufferedInputStream(new FileInputStream(file));  
            int bytes = (int) file.length();  
            byte[] buffer = new byte[bytes];  
            int readBytes = bis.read(buffer);  
            if (readBytes != buffer.length) {  
                throw new IOException("Entire file not read");  
            }  
            return buffer;  
        } 
        finally
        {  
            if (bis != null)
            {  
                bis.close();  
            }  
        }  
    }  
   
    /** 
     * 删除当前用户 
     *  
     * @return 
     */ 
    public boolean deleteAccount()
    {  
        if (mConnection == null)  
            return false; 
        
        try
        {  
            mConnection.getAccountManager().deleteAccount();  
            return true;  
        }
        catch (XMPPException e)
        {  
            return false;  
        }  
    }  
   
    /** 
     * 修改密码 
     *  
     * @return 
     */ 
    public boolean changePassword(String pwd)
    {  
        if (mConnection == null)  
            return false;  
        
        try
        {  
            mConnection.getAccountManager().changePassword(pwd);  
            return true;  
        }
        catch (XMPPException e)
        {  
            return false;  
        }  
    }  
   
    /** 
     * 初始化会议室列表 
     */ 
    public List<HostedRoom> getHostRooms()
    {  
        if (mConnection == null)  
            return null;  
        
        Collection<HostedRoom> hostrooms = null;  
        List<HostedRoom> roominfos = new ArrayList<HostedRoom>();  
        try
        {  
            new ServiceDiscoveryManager(mConnection);  
            hostrooms = MultiUserChat.getHostedRooms(mConnection,  
                    mConnection.getServiceName());  
            
            for (HostedRoom entry : hostrooms)
            {  
                roominfos.add(entry);  
                TraceLog.Print_D("room：" + entry.getName() + " - ID:" + entry.getJid());  
            }  
            TraceLog.Print_D("service room number:" + roominfos.size());  
        }
        catch (XMPPException e)
        {  
            e.printStackTrace();  
        }  
        return roominfos;  
    }  
   
    /** 
     * 创建房间 
     *  
     * @param roomName 
     *            房间名称 
     */ 
    public MultiUserChat createRoom(String user, String roomName,  
            String password)
    {  
        if (mConnection == null)  
            return null;  
   
        MultiUserChat muc = null;  
        try
        {  
            // 创建一个MultiUserChat  
            muc = new MultiUserChat(mConnection, roomName + "@conference." 
                    + mConnection.getServiceName());  
            // 创建聊天室  
            muc.create(roomName);  
            // 获得聊天室的配置表单  
            Form form = muc.getConfigurationForm();  
            // 根据原始表单创建一个要提交的新表单。  
            Form submitForm = form.createAnswerForm();  
            // 向要提交的表单添加默认答复  
            
            for (Iterator<FormField> fields = form.getFields(); fields  
                    .hasNext();)
            {  
                FormField field = (FormField) fields.next();  
                if (!FormField.TYPE_HIDDEN.equals(field.getType())  
                        && field.getVariable() != null)
                {  
                    // 设置默认值作为答复  
                    submitForm.setDefaultAnswer(field.getVariable());  
                }  
            }  
            
            // 设置聊天室的新拥有者  
            List<String> owners = new ArrayList<String>();  
            owners.add(mConnection.getUser());// 用户JID  
            submitForm.setAnswer("muc#roomconfig_roomowners", owners);  
            // 设置聊天室是持久聊天室，即将要被保存下来  
            submitForm.setAnswer("muc#roomconfig_persistentroom", true);  
            // 房间仅对成员开放  
            submitForm.setAnswer("muc#roomconfig_membersonly", false);  
            // 允许占有者邀请其他人  
            submitForm.setAnswer("muc#roomconfig_allowinvites", true); 
            
            if (!password.equals(""))
            {  
                // 进入是否需要密码  
                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  
                        true);  
                // 设置进入密码  
                submitForm.setAnswer("muc#roomconfig_roomsecret", password);  
            }  
            
            // 能够发现占有者真实 JID 的角色  
            // submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
            // 登录房间对话  
            submitForm.setAnswer("muc#roomconfig_enablelogging", true);  
            // 仅允许注册的昵称登录  
            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);  
            // 允许使用者修改昵称  
            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);  
            // 允许用户注册房间  
            submitForm.setAnswer("x-muc#roomconfig_registration", false);  
            // 发送已完成的表单（有默认值）到服务器来配置聊天室  
            muc.sendConfigurationForm(submitForm);  
        }
        catch (XMPPException e)
        {  
            e.printStackTrace();  
            return null;  
        }  
        return muc;  
    }  
   
    /** 
     * 加入会议室 
     *  
     * @param user 
     *            昵称 
     * @param password 
     *            会议室密码 
     * @param roomsName 
     *            会议室名 
     */ 
    public MultiUserChat joinMultiUserChat(String user, String roomsName,  
            String password)
    {  
        if (mConnection == null)  
            return null; 
        
        try
        {  
            // 使用XMPPConnection创建一个MultiUserChat窗口  
            MultiUserChat muc = new MultiUserChat(mConnection, roomsName  
                    + "@conference." + mConnection.getServiceName());  
            // 聊天室服务将会决定要接受的历史记录数量  
            DiscussionHistory history = new DiscussionHistory();  
            history.setMaxChars(0);  
            // history.setSince(new Date());  
            // 用户加入聊天室  
            muc.join(user, password, history,  
                    SmackConfiguration.getPacketReplyTimeout());  
            TraceLog.Print_D("join room:" + roomsName + " successful.");  
            return muc;  
        }
        catch (XMPPException e)
        {  
            TraceLog.Print_E("join room:" + roomsName + " failed.");  
            e.printStackTrace();  
            return null;  
        }  
    }  
   
    /** 
     * 查询会议室成员名字 
     *  
     * @param muc 
     */ 
    public List<String> findMulitUser(MultiUserChat muc)
    {  
        if (mConnection == null)  
            return null;  
        
        List<String> listUser = new ArrayList<String>();  
        Iterator<String> it = muc.getOccupants();  
        // 遍历出聊天室人员名称  
        while (it.hasNext())
        {  
            // 聊天室成员名字  
            String name = StringUtils.parseResource(it.next());  
            listUser.add(name);  
        }  
        return listUser;  
    }  
   
    /** 
     * 发送文件 
     *  
     * @param user 
     * @param filePath 
     */ 
    public void sendFile(String user, String filePath)
    {  
        if (mConnection == null)  
            return;  
        // 创建文件传输管理器  
        FileTransferManager manager = new FileTransferManager(mConnection);  
   
        // 创建输出的文件传输  
        OutgoingFileTransfer transfer = manager  
                .createOutgoingFileTransfer(user);  
   
        // 发送文件  
        try
        {  
            transfer.sendFile(new File(filePath), "You won't believe this!");  
        }
        catch (XMPPException e)
        {  
            e.printStackTrace();  
        }  
    }  
   
    /** 
     * 获取离线消息 
     *  
     * @return 
     */ 
    public Map<String, List<HashMap<String, String>>> getHisMessage()
    {  
        if (mConnection == null)  
            return null;  
        
        Map<String, List<HashMap<String, String>>> offlineMsgs = null;  
   
        try
        {  
            OfflineMessageManager offlineManager = new OfflineMessageManager(  
                    mConnection);  
            Iterator<Message> it = offlineManager.getMessages();  
   
            int count = offlineManager.getMessageCount();  
            if (count <= 0)  
                return null;  
            offlineMsgs = new HashMap<String, List<HashMap<String, String>>>();  
   
            while (it.hasNext())
            {  
                Message message = it.next();  
                String fromUser = StringUtils.parseName(message.getFrom());  
                ;  
                HashMap<String, String> histrory = new HashMap<String, String>();  
                histrory.put("useraccount",  
                        StringUtils.parseName(mConnection.getUser()));  
                histrory.put("friendaccount", fromUser);  
                histrory.put("info", message.getBody());  
                histrory.put("type", "left"); 
                
                if (offlineMsgs.containsKey(fromUser))
                {  
                    offlineMsgs.get(fromUser).add(histrory);  
                } 
                else
                {  
                    List<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();  
                    temp.add(histrory);  
                    offlineMsgs.put(fromUser, temp);  
                }  
            }  
            offlineManager.deleteMessages();  
        } 
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
        return offlineMsgs;  
    }  
       
    /** 
     * 判断OpenFire用户的状态 strUrl :  
     * url格式 - http://my.openfire.com:9090/plugins/presence 
     * /status?jid=user1@SERVER_NAME&type=xml  
     * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线  
     * 说明 ：必须要求 OpenFire加载 presence 插件，同时设置任何人都可以访问 
     */    
    public int IsUserOnLine(String user)
    {  
        String url = "http://"+mXmppCfg.GetHostIP()+":9090/plugins/presence/status?" +  
                "jid="+ user +"@"+ mXmppCfg.GetServiceName() +"&type=xml";  
        int shOnLineState = 0; // 不存在  
        try
        {  
            URL oUrl = new URL(url);  
            URLConnection oConn = oUrl.openConnection();  
            if (oConn != null)
            {  
                BufferedReader oIn = new BufferedReader(new InputStreamReader(  
                        oConn.getInputStream())); 
                
                if (null != oIn)
                {  
                    String strFlag = oIn.readLine();  
                    oIn.close();  
                    System.out.println("strFlag"+strFlag);  
                    if (strFlag.indexOf("type=\"unavailable\"") >= 0)
                    {  
                        shOnLineState = 2;  
                    } 
                    
                    if (strFlag.indexOf("type=\"error\"") >= 0)
                    {  
                        shOnLineState = 0;  
                    }
                    else if (strFlag.indexOf("priority") >= 0 
                            || strFlag.indexOf("id=\"") >= 0)
                    {  
                        shOnLineState = 1;  
                    }  
                }  
            }  
        }
        catch (Exception e)
        {  
            e.printStackTrace();  
        }  
   
        return shOnLineState;  
    }  
   
    /** 
     * 加入providers的函数 ASmack在/META-INF缺少一个smack.providers 文件 
     *  
     * @param pm 
     */ 
    public void configureConnection(ProviderManager pm)
    {  
   
        // Private Data Storage  
        pm.addIQProvider("query", "jabber:iq:private",  
                new PrivateDataManager.PrivateDataIQProvider());  
   
        // Time  
        try
        {  
            pm.addIQProvider("query", "jabber:iq:time",  
                    Class.forName("org.jivesoftware.smackx.packet.Time"));  
        }
        catch (ClassNotFoundException e) 
        {  
            TraceLog.Print_E("Can't load class for org.jivesoftware.smackx.packet.Time");  
            e.printStackTrace();
        }  
   
        // Roster Exchange  
        pm.addExtensionProvider("x", "jabber:x:roster",  
                new RosterExchangeProvider());  
   
        // Message Events  
        pm.addExtensionProvider("x", "jabber:x:event",  
                new MessageEventProvider());  
   
        // Chat State  
        pm.addExtensionProvider("active",  
                "http://jabber.org/protocol/chatstates",  
                new ChatStateExtension.Provider());  
        pm.addExtensionProvider("composing",  
                "http://jabber.org/protocol/chatstates",  
                new ChatStateExtension.Provider());  
        pm.addExtensionProvider("paused",  
                "http://jabber.org/protocol/chatstates",  
                new ChatStateExtension.Provider());  
        pm.addExtensionProvider("inactive",  
                "http://jabber.org/protocol/chatstates",  
                new ChatStateExtension.Provider());  
        pm.addExtensionProvider("gone",  
                "http://jabber.org/protocol/chatstates",  
                new ChatStateExtension.Provider());  
   
        // XHTML  
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",  
                new XHTMLExtensionProvider());  
   
        // Group Chat Invitations  
        pm.addExtensionProvider("x", "jabber:x:conference",  
                new GroupChatInvitation.Provider());  
   
        // Service Discovery # Items  
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",  
                new DiscoverItemsProvider());  
   
        // Service Discovery # Info  
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",  
                new DiscoverInfoProvider());  
   
        // Data Forms  
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());  
   
        // MUC User  
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",  
                new MUCUserProvider());  
   
        // MUC Admin  
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",  
                new MUCAdminProvider());  
   
        // MUC Owner  
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",  
                new MUCOwnerProvider());  
   
        // Delayed Delivery  
        pm.addExtensionProvider("x", "jabber:x:delay",  
                new DelayInformationProvider());  
   
        // Version  
        try
        {  
            pm.addIQProvider("query", "jabber:iq:version",  
                    Class.forName("org.jivesoftware.smackx.packet.Version"));  
        }
        catch (ClassNotFoundException e)
        {  
            // Not sure what's happening here.  
        }  
   
        // VCard  
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());  
   
        // Offline Message Requests  
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline",  
                new OfflineMessageRequest.Provider());  
   
        // Offline Message Indicator  
        pm.addExtensionProvider("offline",  
                "http://jabber.org/protocol/offline",  
                new OfflineMessageInfo.Provider());  
   
        // Last Activity  
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());  
   
        // User Search  
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());  
   
        // SharedGroupsInfo  
        pm.addIQProvider("sharedgroup",  
                "http://www.jivesoftware.org/protocol/sharedgroup",  
                new SharedGroupsInfo.Provider());  
   
        // JEP-33: Extended Stanza Addressing  
        pm.addExtensionProvider("addresses",  
                "http://jabber.org/protocol/address",  
                new MultipleAddressesProvider());  
   
        // FileTransfer  
        pm.addIQProvider("si", "http://jabber.org/protocol/si",  
                new StreamInitiationProvider());  
   
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",  
                new BytestreamsProvider());  
   
        // Privacy  
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());  
        pm.addIQProvider("command", "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider());  
        pm.addExtensionProvider("malformed-action",  
                "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider.MalformedActionError());  
        pm.addExtensionProvider("bad-locale",  
                "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider.BadLocaleError());  
        pm.addExtensionProvider("bad-payload",  
                "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider.BadPayloadError());  
        pm.addExtensionProvider("bad-sessionid",  
                "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider.BadSessionIDError());  
        pm.addExtensionProvider("session-expired",  
                "http://jabber.org/protocol/commands",  
                new AdHocCommandDataProvider.SessionExpiredError());  
    } 
}
