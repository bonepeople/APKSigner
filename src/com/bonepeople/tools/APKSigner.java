package com.bonepeople.tools;

import java.io.IOException;
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
	private static final String CHANNELFILE = "UMENG_CHANNEL.txt";
	private static String[] _channel_names;

	public APKSigner()
	{
		get_channel_name();
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

	public static void main(String[] args)
	{
		new APKSigner();
	}
}
