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
import java.net.URLDecoder;
import com.google.gson.*;

public class JsonPage implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI requestURI = exchange.getRequestURI();
		String response;

		List<String> strlist = new ArrayList<String>();

		String requestMethod = exchange.getRequestMethod();

		String query = requestURI.getQuery();

		strlist.add("text/json");

		if (requestMethod.compareTo("GET") == 0 || requestMethod.compareTo("get") == 0) {
			response = getLocalPage(requestURI, query);
			exchange.getResponseHeaders().put("content-type", strlist);
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else if (requestMethod.compareTo("POST") == 0 || requestMethod.compareTo("post") == 0) {
			response = CreateLocalPage(requestURI, query);
			exchange.getResponseHeaders().put("content-type", strlist);
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else if (requestMethod.compareTo("PUT") == 0 || requestMethod.compareTo("put") == 0) {
			response = UpdateLocalPage(requestURI, query);
			exchange.getResponseHeaders().put("content-type", strlist);
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else if (requestMethod.compareTo("DELETE") == 0 || requestMethod.compareTo("delete") == 0) {
			response = DeleteLocalPage(requestURI, query);
			exchange.getResponseHeaders().put("content-type", strlist);
			exchange.sendResponseHeaders(200, response.getBytes().length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} else {
			System.out.println("Operation not supported!");
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			os.close();
		}
	}

	// metodo usato da DELETE
	private String DeleteLocalPage(URI pageid, String query) {
		String urlPage = pageid.toString().substring(6);
		int indexQuery = urlPage.indexOf("?");
		urlPage = indexQuery != -1 ? urlPage.substring(0, indexQuery) : urlPage;
		String page = "./Root" + urlPage;
		query = query != null ? query.substring(5) : null;
		// apro file e provo ad eliminarlo
		File daEliminare = new File(page + "/" + query + ".json");
		boolean res = daEliminare.delete();
		if (res)
			return "Record Eliminato con successo";
		else
			return "Record NON Esistente!";
	}

	// metodo usato da UPDATE
	private String UpdateLocalPage(URI pageid, String query) {
		String urlPage = pageid.toString().substring(6);
		// tengo solo percorso da seguire rimuovendo da pageid la query se presente
		int indexQuery = urlPage.indexOf("?");
		urlPage = urlPage.substring(0, indexQuery);
		String page = "./Root" + urlPage;
		String answer = "/index ";
		String response = "";
		String namefile = "";
		String line = "";
		double version = 1.0;
		String[] value = query.split("&");
		// prendo nome file da modificare e versione richiesta
		for (String values : value) {
			if (values.contains("type=")) {
				namefile = values.substring(5);
			}
			if (values.contains("version=")) {
				version = Double.parseDouble(values.split("=")[1]);
			}
		}
		Gson gson = new GsonBuilder().setVersion(version).create();
		// leggo il vecchio contenuto del file
		File file = new File(page + "/" + namefile + ".json");
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				response += line;
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + page + "'");
			return "fail";
		} catch (IOException ex) {
			System.out.println("Error reading file '" + page + "'");
			return "fail";
		}
		// trasformo il file json nell'oggetto ferramenta
		Ferramenta ferramenta = gson.fromJson(response, Ferramenta.class);
		String[] tempo;
		Hashtable<String, String> table = new Hashtable<>();
		// prendo i nuovi valori da modificare salvandoli in un hashtable
		for (String valuess : value) {
			tempo = valuess.split("=");
			if (tempo[0].contains("type"))
				continue;
			else
				table.put(tempo[0], tempo[1]);
		}
		// aggiorno i nuovi valori
		ferramenta.updateID(table.get("id"));
		ferramenta.updateN(Integer.valueOf(table.get("N")));
		response = gson.toJson(ferramenta);
		// scrivo i nuovi valori sul file json
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(response);
			bufferedWriter.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + page + "'");
			return "fail";
		} catch (IOException ex) {
			System.out.println("Error reading file '" + page + "'");
			return "fail";
		}
		answer = "Modificato file:" + namefile + " nell'URI:" + urlPage;
		return answer;
	}

	// metodo usato da POST
	private String CreateLocalPage(URI pageid, String query) {
		String urlPage = pageid.toString().substring(6);
		int indexQuery = urlPage.indexOf("?");
		urlPage = urlPage.substring(0, indexQuery);
		String page = "./Root" + urlPage;
		String answer = "/index ";
		String[] value = query.split("&");
		String namefile = "";
		double version = 1.0;
		// prendo nome file e versione richiesta
		for (String values : value) {
			if (values.contains("type=")) {
				namefile = values.substring(5);
			}
			if (values.contains("version=")) {
				version = Double.parseDouble(values.split("=")[1]);
			}
		}
		Gson gson = new GsonBuilder().setVersion(version).create();
		String[] tempo;
		Hashtable<String, String> table = new Hashtable<>();
		// salvo in hashtable i valori da inserire
		for (String valuess : value) {
			tempo = valuess.split("=");
			if (tempo[0].contains("type") || tempo[0].contains("version"))
				continue;
			else
				table.put(tempo[0], tempo[1]);
		}
		// creo oggetto ferramenta
		Ferramenta ferramenta = new Ferramenta(table.get("id").toString(), Integer.valueOf(table.get("N")),
				Boolean.parseBoolean(table.get("Usato")));
		// creo nuovo file
		File fail = new File(page + "/" + namefile + ".json");
		try {
			fail.createNewFile();
		} catch (IOException ex) {
			System.out.println("Errore nella creazione del file");
		}
		// scrivo valori nel file json
		try {
			FileWriter fileWriter = new FileWriter(fail);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(gson.toJson(ferramenta));
			bufferedWriter.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + page + "'");
			return "fail";
		} catch (IOException ex) {
			System.out.println("Error reading file '" + page + "'");
			return "fail";
		}
		answer = "Creato nuovo record nell'URI:" + urlPage;
		return answer;
	}

	// metodo usato da GET
	private static String getLocalPage(URI pageid, String query) {
		String line;

		String urlPage = pageid.toString().substring(6);
		int indexQuery = urlPage.indexOf("?");
		urlPage = indexQuery != -1 ? urlPage.substring(0, indexQuery) : urlPage;
		String page = "./Root" + urlPage;
		String answer = "/index ";
		String response = "";
		String filename = "";
		Double version = 1.0;
		// se ho query allora viene richiesto un file preciso
		if (query != null) {
			String[] values = query.split("&");
			// prendo nome file e versione richiesta
			for (String value : values) {
				if (value.contains("version"))
					version = Double.parseDouble(value.split("=")[1]);
				if (value.contains("type"))
					filename = value.split("=")[1];
			}
		}
		Gson gson = new GsonBuilder().setVersion(version).create();
		// se file name="" allora non e' richiesto un file, quindi metto a null
		// altrimenti tengo il nome del file trovato
		filename = filename.equals("") ? null : filename;
		List<String> results = new ArrayList<String>();
		// prendo tutti i file contenuti nel percorso indicato
		File[] files = new File(page).listFiles();
		for (File file : files) {
			// se e' un file ed e' quello con il nome richiesto dalla query
			if ((file.isFile())
					&& (file.getName().substring(0, file.getName().toString().length() - 5).equals(filename))) {
				// leggo il file
				try {
					FileReader fileReader = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					while ((line = bufferedReader.readLine()) != null) {
						response += line;
					}
					bufferedReader.close();
					//ritrasformo in ferramenta per poter prendere la versione giusta
					Ferramenta trovato = gson.fromJson(response, Ferramenta.class);
					//riporto a json per poi restituirlo
					response = gson.toJson(trovato);
					return response;
				} catch (FileNotFoundException ex) {
					System.out.println("Unable to open file '" + page + "'");
					return "fail";
				} catch (IOException ex) {
					System.out.println("Error reading file '" + page + "'");
					return "fail";
				}
				// altrimenti se e' un file ma non quello richiesto dalla query
			} else if (file.isFile()) {
				// indico come scrivere la query per cercarlo successivamente
				results.add("/index" + file.toString().substring(6).substring(0, urlPage.length()).replace("\\", "/")
						+ "/?type=" + file.getName().substring(0, file.getName().toString().length() - 5));
			} else
				// se non e' file allora e' una directory
				results.add("/index" + file.toString().substring(6).replace("\\", "/"));

		}
		// trasformo l'array che contiene i percorsi trovati in json e lo restituisco
		answer = gson.toJson(results).replace("[", "{").replace("]", "}");
		return answer;
	}
}
