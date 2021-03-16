package Lab8;

import Lab7.URLDepthPair;

import java.util.*;
import java.io.*;
import java.net.*;


public class Crawler1 {
    static int numThreads = 10;
    static int timeOut = 300;
    public static void main(String[] args) {
        //ссылка для отправки запроса
        URLDepthPair currentDepthPair = new URLDepthPair("https://www.google.ru/");
        ArrayList<String> seenURLs = new ArrayList<String>();
        seenURLs.add(currentDepthPair.getURL());

        //определение глубины поиска
        URLPool pool = new URLPool(2);
        pool.put(currentDepthPair);

        int activeAmount = Thread.activeCount();

        while (pool.getWaitThreads() != numThreads) {

            if (Thread.activeCount() - activeAmount < numThreads) {
                CrawlerTask crawler = new CrawlerTask(pool);
                new Thread(crawler).start();
            }
            else {
                try {
                    Thread.sleep(100);  // 0.1 second
                }
                catch (InterruptedException ie) {
                    System.out.println("Caught unexpected: InterruptedException, ignoring...");
                }

            }
        }
        for(String s : pool.seenURLs) {
            System.out.println(s);
        }

        System.out.println(pool.seenURLs.size());
        System.exit(0);

    }

    public static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {

        LinkedList<String> URLs = new LinkedList<String>();
        Socket sock;
        //Создаем сокет для сервера
        try {
            sock = new Socket(myDepthPair.getWebHost(), 80);
        }
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        catch (IOException ex) {
            System.err.println("IOException: " + ex.getMessage());
            return URLs;
        }

        try {
            sock.setSoTimeout(timeOut);
        }
        catch (SocketException exc) {
            System.err.println("SocketException: " + exc.getMessage());
            return URLs;
        }

        String docPath = myDepthPair.getDocPath();
        String webHost = myDepthPair.getWebHost();

        OutputStream outStream;

        try {
            outStream = sock.getOutputStream();
        }
        catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }

        PrintWriter req = new PrintWriter(outStream, true);
        req.println("GET " + docPath + " HTTP/1.1");
        req.println("Host: " + webHost);
        req.println("Connection: close");
        req.println();
        //Обработка данных, отправленных сервером
        InputStream inStream;
        try {
            inStream = sock.getInputStream();
        }
        catch (IOException e){
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);


        //поиск ссылок в ответе сервера
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                return URLs;
            }

            if (line == null)
                break;
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;

            while (true) {
                String START_URL = "a href=\"";
                String END_URL = "\"";

                index = line.indexOf(START_URL, index);
                if (index == -1)
                    break;

                index += START_URL.length();
                beginIndex = index;

                endIndex = line.indexOf(END_URL, index);
                index = endIndex;

                try {
                    String newLink = line.substring(beginIndex, endIndex);
                    if(URLs.contains(newLink))
                        continue;

                    if(newLink.startsWith("http")) {
                        URLs.add(newLink);
                    }else if(newLink.startsWith("tel")) {

                    }else {
                        if(newLink.startsWith("/"))
                            URLs.add("http://"+webHost+""+newLink);
                        else
                            URLs.add("http://"+webHost+"/"+newLink);
                    }

                }catch(Exception e) {
                    System.err.println("error while substringing: "+beginIndex + " " + endIndex);
                    break;
                }

            }

        }
        return URLs;
    }
}

