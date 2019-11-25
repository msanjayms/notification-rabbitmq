package org.mylearning.notification.sms;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageServerAPI {
	public static EnMessage sendMessage(EnMessage enMessage, String host, int port) throws IOException {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			String data = readByte(in);
			out.write(enMessage.toXML().getBytes(), 0, enMessage.toXML().length());
			out.flush();
			data = readByte(in);
			return EnMessage.fromXML(data);			
		} catch(IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(socket!=null)
				socket.close();
		}
	}
	
	private static String readByte(BufferedInputStream in) throws IOException {
		int i = in.read();
		StringBuffer dataGot = new StringBuffer();
		if(i!=-1) {
			//System.out.println("Got 1 char : "+(char)i);
			dataGot.append((char)i);
			int l = in.available();
			if(l>0) { 
				byte[] byteData = new byte[l];
				in.read(byteData);
				dataGot.append(new String(byteData));
			}
			return dataGot.toString();
		} else {
			return null;
		}
	}

}
