package com.bonepeople.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * APK文件的签名工具
 * 
 * @author bonepeople
 */
public class APKSigner
{
	private static final String CHARSET = "GBK";// 这个是Windows下的默认字符集，输出信息不正常时可以调整此项
	private static final String CHANNELFILE = "UMENG_CHANNEL.txt";// 这个是渠道文件的文件路径
	private static final String KEYSTOREFILE = "key";// 这个是密钥文件的文件路径
	public static final String STOREPASS = Config.STOREPASS;// 这个是密钥文件的密码
	public static final String KEYNAME = Config.KEYNAME;// 这个是密钥中签名的别名
	private static String[] _channel_names;

	public APKSigner()
	{
		get_channel_name();
		for (String _channel_name : _channel_names)
		{
			sign(_channel_name);
		}
	}

	private void get_channel_name()
	{
		FileChannel _filechannel = null;
		try
		{
			String _content = "";
			_filechannel = FileChannel.open(Paths.get(CHANNELFILE), StandardOpenOption.READ);
			ByteBuffer _buffer = ByteBuffer.allocate(32);
			while (_filechannel.read(_buffer) != -1)
			{
				_buffer.flip();
				CharBuffer _chars = Charset.forName("UTF-8").decode(_buffer);
				_content += _chars.toString();
				_buffer.clear();
			}
			_channel_names = _content.split(System.getProperty("line.separator"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (_filechannel != null)
				try
				{
					_filechannel.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
		}
	}

	private void sign(String _channel_name)
	{
		if (new File(get_unsignedFilePath(_channel_name)).exists())
		{
			String _cmd = "jarsigner -keystore " + KEYSTOREFILE + " -storepass " + STOREPASS + " -signedjar " + get_signedFilePath(_channel_name) + " " + get_unsignedFilePath(_channel_name) + " "
					+ KEYNAME;
			InputStream _in = null;
			try
			{
				System.out.println("正在签名：" + get_signedFilePath(_channel_name));
				Process _process = Runtime.getRuntime().exec(_cmd);
				_in = _process.getInputStream();
				BufferedReader _reader = new BufferedReader(new InputStreamReader(_in, Charset.forName(CHARSET)));
				try
				{
					String _line = "";
					while ((_line = _reader.readLine()) != null)
					{
						System.out.println(_line);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				_reader.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println(get_unsignedFilePath(_channel_name) + " NotFound");
		}
	}

	/**
	 * 获取对应渠道未签名文件的路径，可以在这里自定义文件名
	 */
	private String get_unsignedFilePath(String _channel_name)
	{
		return "unsigned\\" + _channel_name + ".apk";
	}

	/**
	 * 获取对应渠道已签名文件的路径，可以在这里自定义文件名
	 */
	private String get_signedFilePath(String _channel_name)
	{
		return "signed\\" + _channel_name + "_" + Config._version + ".apk";
	}

	public static void main(String[] args)
	{
		new APKSigner();
	}
}
