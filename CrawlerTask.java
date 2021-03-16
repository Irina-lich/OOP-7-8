package Lab8;

import Lab7.URLDepthPair;

import java.util.*;

//через интерфейс Runnable реализуется многопоточность
//также могопоточность можно реализовать, наследуя класс Thread
public class CrawlerTask implements Runnable {


    public URLDepthPair depthPair;


    public URLPool myPool;

    public CrawlerTask(URLPool pool) {
        myPool = pool;
    }


    //выполняется поиск ссылок на странице в отдельном потоке
    public void run() {

        depthPair = myPool.get();

        int myDepth = depthPair.getDepth();

        //возврщает список ссылок на сайте
        LinkedList<String> linksList = new LinkedList<String>();
        linksList = Crawler1.getAllLinks(depthPair);

        for (int i=0;i<linksList.size();i++) {
            String newURL = linksList.get(i);

            URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
            myPool.put(newDepthPair);
        }
    }

}
