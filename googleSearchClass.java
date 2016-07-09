/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webcrawler;

/**
 *
 * @author pradeep
 */

import java.util.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class googleSearchClass {
    
    static ArrayList<String> googleResults ;       //storing results from google search.
    
    static int count = 0;
    
    public static String search() throws Exception 
    {
        googleResults = new ArrayList<>() ;
        
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter String to search: ");
        
        String google = "https://www.google.com/search?client=ubuntu&channel=fs&q=";
        String search = sc.nextLine();               //string to search
        String charset = "UTF-8";
        String userAgent = "Mozilla";
        
        System.out.println("Searching on Google ................ ");
        
        
            /////////////////////////////////////////////////////
        
            
        Elements links = Jsoup.connect(google+ URLEncoder.encode(search,charset)+"&num=150").timeout(0).userAgent(userAgent).get().select(".g>.r>a");
        
        for( Element link : links )
        {
            String url = link.absUrl("href");
            
            //decode link from link of google.
            String urlLink = URLDecoder.decode(url.substring(url.indexOf("=")+1 , url.indexOf("&")) , charset);
            
            if( ! urlLink.startsWith("http") )
            {
                continue;    //ads/news etc
            }
            
            count++ ;
            
            System.out.println("URL ("+count+") : "+urlLink);   //link of website
            
            googleResults.add(urlLink) ;
        }
        
        
        /////////////////////////////////////////////////////////////
        
        
        if( count == 0 )
        {
            System.out.println("\nNo result found.\nExiting....");
            System.exit(0);
        }
        
        System.out.print("\n\nSelect one out of "+ count +" results : ");
        
        int choice = sc.nextInt() ;
        
        if( choice > count || choice < 1 )
        {
            System.out.println("Invalid choice . Default First result.");
            choice = 1 ;
        }
        
        //System.out.println("Url : "+googleResults.get(choice-1));
        
        return googleResults.get(choice-1) ;
    }
}