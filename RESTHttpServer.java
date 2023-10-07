import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.*;
import java.util.*;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RESTHttpServer {

        static public int port=3000;

	public static void main(String[] args) throws IOException {
		if(args.length>1 && args[0].equals("-port")) try {
			port = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
		HttpServer server=HttpServer.create(new InetSocketAddress(port),0);
		server.createContext("/index", new JsonPage());
		server.setExecutor(threadPoolExecutor);
		server.start();
	}
}

