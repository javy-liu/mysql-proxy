package com.mysql.proxy;

import java.io.IOException;

public class Main
{

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
		// TODO 자동 생성된 메소드 스텁
		int listenPort = 33006;
		String plugins = "";

		Engine result = new Engine(listenPort, plugins);
		System.out.println(result);
	}

}
