import com.sun.deploy.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main (String[] args) throws IOException {
        String url = "https://rozetka.com.ua/ua/universalnye-mobilnye-batarei/c387969/";
        Document doc = Jsoup.connect(url).get();
        Elements pages = doc.select("a.paginator-catalog-l-link");
        int num = Integer.parseInt(pages.last().text());
        for (int i = 1; i < num + 1; i++){
            String pg = url + "page=" + Integer.toString(i) + "/";
            parse_category_page(pg);
        }
    }

    private static void parse_category_page (String url) throws IOException{
        Document doc = Jsoup.connect(url).get();
        Elements elem = doc.select("div.g-i-tile-i-title");
        for(Element el: elem){
            String link = el.select("a").attr("href") + "comments/";
            parse_reviews(link);
        }
    }

    public static void parse_reviews (String url) throws IOException{
        Document doc = Jsoup.connect(url).get();
        Elements nums = doc.select("a.paginator-catalog-l-link");
        URL new_url = new URL(url);
        String[] link = new_url.getPath().split("/");
        String title = link[2];
        String filename = String.format("%s.csv", title);
        FileWriter writer;
        writer = new FileWriter(filename, true);
        int num = 0;
        if (nums.size() != 0){
            num = Integer.parseInt(nums.last().text());
        }
        List<String> sentiments = new ArrayList<String>();
        for (int i=0; i < num; i++){
            String pg = url + "page=" + Integer.toString(i + 1) + "/";
            List<String> sentiment = parse_reviews_page(pg);
            sentiments.addAll(sentiment);
        }
        for (int i=0; i<sentiments.size(); i++){
            writer.write(sentiments.get(i));
            writer.write("\n");
        }

    }

    public static List<String> parse_reviews_page(String url)throws IOException{
        Document doc = Jsoup.connect(url).get();
        Elements reviews = doc.select("article.pp-review-i");
        List<String> sentiments = new ArrayList<String>();
        for(Element review: reviews){
            String star = review.select("span.g-rating-stars-i").attr("content");
            String text = review.select("div.pp-review-text-i").text();
            if (star!= ""){
            sentiments.add(star + "," + text + "\n");
            System.out.println(star + "," + text + "\n");

            }
        }

        return sentiments;
        }
    }