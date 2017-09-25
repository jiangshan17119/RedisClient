# RedisClient

package com;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisClient {
	public static void main(String[] args) {
		String redisIp = "10.86.51.253";
		String smbFileIp = "10.86.10.103";
		
		ThreadPass pass1 = new ThreadPass(redisIp,smbFileIp,"8512880081", "I");
		ThreadPass pass2 = new ThreadPass(redisIp,smbFileIp,"8512880082", "E");
		ThreadPass pass3 = new ThreadPass(redisIp,smbFileIp,"8512880083", "I");
		ThreadPass pass4 = new ThreadPass(redisIp,smbFileIp,"8512880084", "E");
		ThreadFile tf = new ThreadFile();
		pass1.start();
		pass2.start();
		pass3.start();
		pass4.start();
		tf.run();
	}
}

class ThreadPass extends Thread {
	private String redisIp;
	private String smbFileIp;
	private String pass;
	private String IE;

	public ThreadPass(String redisIp,String smbFileIp,String pass, String IE) {
		this.redisIp = redisIp;
		this.smbFileIp = smbFileIp;
		this.pass = pass;
		this.IE = IE;
	}

	public synchronized void run() {
		Jedis j = new Jedis(redisIp);
		JedisPubSub jps = new JedisPubSub() {
			public void onMessage(String channel, String message) {
				boolean b = false;
				Jedis jp = new Jedis(redisIp);
				StringTools st = new StringTools();
				super.onMessage(channel, message);
				System.out.println(message);
				String DBPath = "D:\\DB\\";
				// String message =
				// "{\"tType\":\"Publish\",\"tData\":\"{\"Cmd\": 33,\"Cmd_desc\": \"0x21\",\"Event\": \"GATHER_INFO\",\"TimeSpan\": \"2016-11-02 11:56:33.319\",\"D_type\": \"JSON\",\"C_Type\": \"Front\",\"AREA_ID\":\"8512880088\",\"CHNL_NO\":\"8512880081\",\"I_E_TYPE\":\"I\",\"SEQ_NO\":\"16110211495114650013\",\"Data\": {\"CAR\":{\"VE_NAME\":\"\",\"CAR_EC_NO\":\"\",\"CAR_EC_NO2\":\"\",\"VE_CUSTOMS_NO\":\"\",\"VE_WT\":\"\"},\"CIMCESEAL\":{\"CIMC_ESEALDATA\":\"\"},\"CONTA\":{\"CIVE_NAME\":\"\",\"CONTA_NUM\":\"\",\"CONTA_RECO\":\"\",\"CONTA_ID_F\":\"\",\"CONTA_ID_B\":\"\"},\"IC\":{\"DR_IC_NO\":\"0900123018\",\"IC_NO\":\"\",\"IC_SEQUENCE\":\"\",\"IC_VEHICLE_NO\":\"\",\"IC_BUSI_MESS\":\"\",\"IC_VEHICLE_TYPE\":\"\",\"IC_ISSUE_TIME\":\"\",\"IC_VALID_TIME\":\"\",\"IC_WEIGHT_INFO\":\"\",\"IC_GOODS_WT\":\"\",\"IC_TRANS_MODE\":\"\",\"TOTAL_AMOUNT\":\"\",\"TRANSFER_DATA\":\"\",\"IS_DIRECT\":\"\",\"TIANKEDATA\":\"\",\"HUADONGDATA\":\"\",\"CIQ_IC_TYPE\":\"\",\"GOODSICTEMPNO\":\"\",\"IC_BILL_NO\":\"\",\"IC_CONTA_ID\":\"\",\"IC_CO_CUSTOMS_NO\":\"\",\"IC_DR_CUSTOMS_NO\":\"\",\"IC_ESEAL_ID\":\"\",\"IC_GROSS_WT\":\"\",\"IC_VE_CUSTOMS_NO\":\"\",\"IC_VE_NAME\":\"\"},\"PIC\":{\"PICNAME\":\"\"},\"SEAL\":{\"ESEAL_ID\":\"\"},\"VENOIDENTIFY\":{\"VIVE_NAME\":\"\",\"VePICNAME1\":\"\",\"VePICNAME2\":\"\"},\"WEIGHT\":{\"GROSS_WT\":\"\"},\"YTTMVideoCard\":{\"BVEHICLENO\":\"\",\"CVEHICLENO\":\"\",\"FVEHICLENO\":\"\"},\"BarCode\":{\"BarCodeData\":\"\"},\"CHNL_TYPE\":\"\",\"FILE_TIME\":\"2016-11-02 11:56:32\",\"GATHER_FLAG\":\"\",\"USERID\":\"\",\"YWTYPE\":\"\"}}\"}";
				// 订阅报文--------------------------------------------------------------
				String jsonStr = message.replaceAll("\\\\\\\"", "\\\"")
						.replaceAll(" ", "").replaceAll("\n", "");
				String DR_IC_NO = st.getJsonValue(jsonStr, "DR_IC_NO");// ic卡
				String SEQ_NO = st.getJsonValue(jsonStr, "SEQ_NO");// 序列号
				String changzhan = st.getJsonValue(jsonStr, "AREA_ID");// 场站
				String tongdao = st.getJsonValue(jsonStr, "CHNL_NO");// 通道
				String jinchu = st.getJsonValue(jsonStr, "I_E_TYPE");// 进出类型
				String jinchuxml = "";
				if ("E".equals(jinchu)) {
					jinchuxml = "O";
				} else {
					jinchuxml = "I";
				}
				// 写文件--------------------------------------------------------------
				File file = new File(DBPath + DR_IC_NO + ".xml");
				String billType = "";
				if (file.isFile() && file.exists()) {
					b = true;
					try {
						SAXReader reader = new SAXReader();
						Document document1 = reader.read(file);
						String text = document1.asXML();
						String xml = text.replaceAll("\n", "")
								.replaceAll(" ", "").replaceAll("	", "");
						billType = st.getXmlValue(xml, "billType");
						// String xml =
						// "<?xmlversion=\"1.0\"encoding=\"UTF-8\"standalone=\"yes\"?><DxpMessage><MessageHead><messageId>cee1de32-e714-4da6-afa9-7d880423717e</messageId><messageType>BAYONETPASSREC</messageType><senderId>BAYONET_DXP_CUS</senderId><receiverId>SZJET_LMS</receiverId><messageTime>20160804173154111</messageTime><version>v1.0</version></MessageHead><MessageBody><VePassResponse><billNo>68bc854a-c04e-4201-b5f6-221cbe03b344</billNo><billType>1</billType><ioFlag>O</ioFlag><VeicList><Veic><veNo>苏E8B51U</veNo><icNo>1111</icNo></Veic></VeicList><status>O</status><entryTime>20160801100030</entryTime><note>我是张军林</note></VePassResponse></MessageBody></DxpMessage>";
						xml = st.setXmlValue(xml, "icNo", DR_IC_NO);
						xml = st.setXmlValue(xml, "ioFlag", jinchuxml);
						SmbFile remoteFile = new SmbFile(
								"smb://Administrator:Abcd1234@"+smbFileIp+"/receive/"
										+ SEQ_NO + ".xml");
						OutputStream out = new BufferedOutputStream(
								new SmbFileOutputStream(remoteFile));
						int num = xml.indexOf(">");
						String newxml = xml.substring(num + 1);
						Document document = DocumentHelper.parseText(newxml);
						OutputFormat format = new OutputFormat("", true);
						format.setEncoding("utf-8");// 设置编码格式
						XMLWriter xmlWriter = new XMLWriter(out, format);
						xmlWriter.write(document);
						xmlWriter.close();
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// 发布--------------------------------------------------------------
				String Pub0x22 = "{\"tData\":\"{\\\"AREA_ID\\\":\\\""
						+ changzhan
						+ "\\\",\\\"CHNL_NO\\\":\\\""
						+ tongdao
						+ "\\\",\\\"C_Type\\\":\\\"PLATFORM\\\",\\\"Cmd\\\":34,\\\"Cmd_desc\\\":\\\"0X22\\\",\\\"D_Type\\\":\\\"JSON\\\",\\\"Data\\\":{\\\"CHECK_RE"
						+ "SULT\\\":\\\"00000000001002000000\\\",\\\"DR_IC_NO\\\":\\\""
						+ DR_IC_NO
						+ "\\\",\\\"FORM_ID\\\":\\\"\\\",\\\"GPS\\\":{\\\"DEST_CUSTOMS\\\":\\\"\\\",\\\"GPS_ID\\\":\\\"\\\",\\\"ORIGIN_CUSTOMS\\\":\\\"\\\",\\\"VE_NAME\\\":\\\"\\\"},\\\"LE"
						+ "DDISPLAY\\\":\\\"检查放行\\\",\\\"OP_HINT\\\":\\\"检查放行\\\",\\\"PRINT_DATA\\\":\\\"\\\",\\\"SEAL\\\":{\\\"ESEAL_ID\\\":\\\"\\\",\\\"SEAL_KEY\\\":\\\"12345678901234567890\\\",\\\"SEAL_NO\\\":\\\"\\\"},\\\"Weight\\\":\\\"\\\"},\\\"Ev"
						+ "ent\\\":\\\"COMMAND_INFO\\\",\\\"I_E_TYPE\\\":\\\""
						+ jinchu
						+ "\\\",\\\"SEQ_NO\\\":\\\""
						+ SEQ_NO
						+ "\\\",\\\"TimeSpan\\\":\\\"2016-10-3114:46:32\\\"}\",\"tType\":\"publish\"\n}";
				if ("8512880082".equals(pass)) {
					if (!b) {
						Pub0x22 = st.setJsonValue(Pub0x22, "CHECK_RESULT",
								"11000000001002000000");
						Pub0x22 = st.setJsonValue(Pub0x22, "OP_HINT", "未关联单据");
					} else {
						if ("0".equals(billType)) {
							Pub0x22 = st.setJsonValue(Pub0x22, "OP_HINT",
									"空车出区");
						} else if ("1".equals(billType)) {
							Pub0x22 = st.setJsonValue(Pub0x22, "OP_HINT",
									"载货出区");
						}
						Pub0x22 = st.setJsonValue(Pub0x22, "CHECK_RESULT",
								"00000000001001000000");
						if (file.isFile() && file.exists()) {
							file.delete();
						}
					}
				}
				System.out.println(Pub0x22);
				jp.publish("PLAT_FORM/" + changzhan + "/" + tongdao + "/"
						+ jinchu + "/34", Pub0x22);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		System.out.println(pass);
		j.subscribe(jps, "FRONT_GRATH/8512880088/" + pass + "/" + IE + "/33");
	}
}

class ThreadFile extends Thread {
	public ThreadFile() {

	}

	public void run() {
		System.out.println("file");
		StringTools st = new StringTools();
		String path0 = "D:\\share\\";
		String DBPath = "D:\\DB\\";
		String xml = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"standalone=\"yes\"?><DxpMessage><MessageHead><messageId>cee1de32-e714-4da6-afa9-7d880423717e</messageId><messageType>BAYONETPASSREC</messageType><senderId>BAYONET_DXP_CUS</senderId><receiverId>SZJET_LMS</receiverId><messageTime>20160804173154111</messageTime><version>v1.0</version></MessageHead><MessageBody><VePassResponse><billNo>68bc854a-c04e-4201-b5f6-221cbe03b344</billNo><billType>1</billType><ioFlag>O</ioFlag><VeicList><Veic><veNo>苏E8B51U</veNo><icNo>1111</icNo></Veic></VeicList><status>O</status><entryTime>20160801100030</entryTime><note>我是张军林</note></VePassResponse></MessageBody></DxpMessage>";
		while (true) {
			File d = new File(path0);
			File list[] = d.listFiles();
			if (list.length != 0) {
				SAXReader reader = new SAXReader();
				Document document1;
				Document document2;
				String path = path0 + list[0].getName();
				try {
					document1 = reader.read(new File(path));
					String text = document1.asXML();
					String xmlRe = text.replaceAll("\n", "")
							.replaceAll(" ", "").replaceAll("	", "");
					String messageId = st.getXmlValue(xmlRe, "messageId");
					String messageType = st.getXmlValue(xmlRe, "messageType");
					String senderId = st.getXmlValue(xmlRe, "senderId");
					String receiverId = st.getXmlValue(xmlRe, "receiverId");
					String messageTime = st.getXmlValue(xmlRe, "messageTime");
					String version = st.getXmlValue(xmlRe, "version");
					String billNo = st.getXmlValue(xmlRe, "billNo");
					String billType = st.getXmlValue(xmlRe, "billType");
					String ioFlag = st.getXmlValue(xmlRe, "ioFlag");
					String veNo = st.getXmlValue(xmlRe, "veNo");
					String icNo = st.getXmlValue(xmlRe, "icNo");
					String status = st.getXmlValue(xmlRe, "status");
					String entryTime = st.getXmlValue(xmlRe, "entryTime");
					String note = st.getXmlValue(xmlRe, "note");
					// 将车辆信息存入数据库
					xml = st.setXmlValue(xml, "messageId", messageId);
					xml = st.setXmlValue(xml, "messageType", messageType);
					xml = st.setXmlValue(xml, "senderId", senderId);
					xml = st.setXmlValue(xml, "receiverId", receiverId);
					xml = st.setXmlValue(xml, "messageTime", messageTime);
					xml = st.setXmlValue(xml, "version", version);
					xml = st.setXmlValue(xml, "billNo", billNo);
					xml = st.setXmlValue(xml, "billType", billType);
					xml = st.setXmlValue(xml, "ioFlag", ioFlag);
					xml = st.setXmlValue(xml, "veNo", veNo);
					xml = st.setXmlValue(xml, "icNo", icNo);
					xml = st.setXmlValue(xml, "status", status);
					xml = st.setXmlValue(xml, "entryTime", entryTime);
					xml = st.setXmlValue(xml, "note", note);
					File remoteFile = new File(DBPath + icNo + ".xml");
					OutputStream out = new BufferedOutputStream(
							new FileOutputStream(remoteFile));
					int num = xml.indexOf(">");
					String newxml = xml.substring(num + 1);
					document2 = DocumentHelper.parseText(newxml);
					OutputFormat format = new OutputFormat("", true);
					format.setEncoding("utf-8");// 设置编码格式
					XMLWriter xmlWriter = new XMLWriter(out, format);
					xmlWriter.write(document2);
					xmlWriter.close();
					out.close();

					File file = new File(path);
					if (file.isFile() && file.exists()) {
						file.delete();
					}
				} catch (Exception e) {
					System.out.println("稍等，文件在被操作");
				}
			}
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
