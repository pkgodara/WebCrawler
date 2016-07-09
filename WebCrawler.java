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
import java.io.*;

/*
  adding jsoup-1.9.2.jar  library
*/

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.Connection.*;

import java.net.URLEncoder;

import java.net.*;
import java.net.URI;
import java.net.URISyntaxException;

import java.awt.Desktop;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

//ssl connection
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509TrustManager.*;

import javax.security.cert.X509Certificate;
import javax.security.cert.CertificateException;


public class WebCrawler {
    
    static ArrayList<String> urlArr ;    //storing urls
    
    static int len = 0 , choice = 1 ;
    static ArrayList<Boolean> marked ;    //marking urls crawled
    
    static ArrayList<String> downloadedImages ;     //storing urls of downloaded images
    static ArrayList<String> imageSearch ;          //storing urls of image search results
    
    static String initUrl , searchText ;
    
    static boolean store = false ;
    
    static File storageFile ;
    
    
    static Scanner sc = new Scanner(System.in) ;   //user input taking
    
    
    
    
    public static void main(String[] args) throws Exception
    {
        urlArr = new ArrayList<>() ;
        marked = new ArrayList<>() ;
        
        downloadedImages = new ArrayList<>();
        
        
        System.out.println("Do you have url : y/yes ;OR use Search-Engines .Enter g/google or b/bing or ya/yahoo or i/gi/images/googleimages :");
        String ch = sc.next() ;
        ch = ch.toLowerCase() ;
        
        if( ch.equals("y") || ch.equals("yes") )
        {
            haveUrl() ;
        }
        else
        {/*
            System.out.print("Enter g/google or b/bing or y/yahoo : ");
            String st = sc.next() ;
            st = st.toLowerCase() ;
            */
            if( ch.equals("g") || ch.equals("google") )
            {
                GoogleSearch() ;
            }
            else
            if( ch.equals("b") || ch.equals("bing") )
            {
                BingSearch() ;
            }
            else
            if( ch.equals("ya") || ch.equals("yahoo") )
            {
                YahooSearch() ;
            }
            else
            if( ch.equals("i") || ch.equals("gi") || ch.equals("images") || ch.equals("googleimages") )
            {
                ImageSearch() ;
            }
            else
            {
                System.out.println("Invalid . Default : Google Search");
                GoogleSearch() ;
            }
        }
        
        if( store )
        {
            storing() ;            //store gathered informations.
        }
        
        
        if( store || choice == 2 )      //ask for opening file.
        {
            System.out.print("Open Containing File/Folder : y/yes ");
            String cs = sc.next() ;
            cs = cs.toLowerCase() ;
            
            if( cs.equals("y") || cs.equals("yes") )
            {
                System.out.println("Opening folder .........");
                openFolder(storageFile) ;
            }
        }
        
        System.out.println("** Done ");                //end of program.
    }
    
    
    
    
    
    
    
    
    
    public static void haveUrl() throws Exception
    {
        
        System.out.print("Enter URL to crawl: ");
        initUrl = sc.next();
        
        
        
        
        start() ;
    }
    
    
    
    
    
    
    
    
    public static void start() throws Exception
    {
        //System.out.println("Enter text to crawl");
        if( ! initUrl.startsWith("http://") && ! initUrl.startsWith("https://") )
            initUrl = "http://".concat(initUrl);
        
        if( initUrl.endsWith("/") )
            initUrl = initUrl.substring(0, initUrl.length()-1 );
        
        
        if( isUrl(initUrl) )
        {
            urlArr.add(initUrl);
            marked.add(Boolean.FALSE);
            len++;
        }
        else
        {
            System.out.println("*** --> This is not a valid URL.");
            System.out.println("Please use Google Search.");
            
            GoogleSearch() ;
        }
        
        
        outputInfo() ;     
    }
    
    
    
    
    
    
    
    
    
    
    public static void outputInfo() throws Exception
    {
        
        System.out.println("Enter 1 to get all links , 2 to download all images , 3 to open page.");
        choice = sc.nextInt() ;
        
        if( choice > 3 || choice < 1 )
        {
            System.out.println("Invalid Entry. Default 1");
            choice = 1;
        }
        
        if( choice == 1 )
        {
            System.out.print("Store links in file : y/yes or n/no  ");
            String cstr = sc.next() ;
            cstr = cstr.toLowerCase() ;
            
            if( cstr.equals("y") || cstr.equals("yes") )
            {
                store = true ;
            }
        }
        
        
        if( choice == 1 || choice == 2 )
        {
            System.out.print("Get urls with specific text : y/yes  ");
            String cstr = sc.next() ;
            cstr = cstr.toLowerCase() ;
            
            if( cstr.equals("y") || cstr.equals("yes") )
            {
                System.out.print("Enter text : (Spaces to differentiate words searches.)");
                sc.nextLine() ;             //new line character ..
                searchText = sc.nextLine() ;
                System.out.println("\nGetting urls containing \""+searchText+"\"\n");
            }
            
            
            crawl();         //crawl across url provided
        }
        
        
        if( choice == 3 )
        {
            System.out.println("Opening Page In Browser........... ");
            openInBrowser(initUrl) ;
        }
        
        
    }
        
        
    

    
    
    


    public static void storing()
    {    
                                                //saves output to file.
        if( store )
        {
            String url = initUrl ;
            url = url.replace("http://", "").replace("https://","").replaceAll("/", "-") ;
            
            File output = new File("/home/pradeep/prog/java/Swing/"+ (url.concat(".Links")) ) ;
            
            
            if( true )
            {
                try
                {
                    if( !output.exists() )
                        output.mkdir() ;
                    
                    File outFile ;
                    if( searchText == null )
                        outFile = new File(output+"/links.txt") ;
                    else
                        outFile = new File(output+"/links("+searchText.replaceAll(" ", ",") +").txt" ) ;
                    
                    FileOutputStream out = new FileOutputStream(outFile) ;
                    
                    outFile.setReadOnly() ;
                    
                    storageFile = outFile ;
                    
                    for( String link : urlArr )
                    {
                        if( matchText( link.toLowerCase() ) )
                            continue ;
                        
                        byte b[] = link.concat("\n").getBytes() ;
                        
                        out.write(b);
                    }
                    out.close();
                    
                    System.out.println("\nOutput Written to File : "+ url.concat(".Links") );
                }
                catch(Exception e)
                {
                    System.out.println("Cannot save output to file.\n"+e);
                }
            }
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    public static void GoogleSearch() throws IOException
    {
        googleSearchClass gs = new googleSearchClass() ;
        
        try
        {
            initUrl = gs.search() ;
            //System.out.println("Url : "+initUrl);
            
            start() ;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("Cannot search google. error : "+e);
        }
    }
    
    
    
    
    
    
    
    public static void BingSearch() throws IOException
    {
        bingSearchClass bs = new bingSearchClass() ;
        
        try
        {
            initUrl = bs.search() ;
            //System.out.println("Url : "+initUrl);
            
            start() ;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("Cannot search google. error : "+e);
        }
    }
    
    
    
    
    
    
    
    public static void YahooSearch() throws IOException
    {
        //yahooSearchClass ys = new yahooSearchClass() ;
        
        try
        {
            //initUrl = ys.search() ;
            System.out.println("temperorily unavailable. use google.");
            GoogleSearch() ;
            //start() ;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("Cannot search google. error : "+e);
        }
    }
    
    
    
    
    public static void ImageSearch() throws IOException
    {
        googleImagesSearch gis = new googleImagesSearch() ;
        
        imageSearch = new ArrayList<>() ;
        
        try
        {
            imageSearch = gis.search() ;
            
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    
    public static boolean matchText( String str )
    {
        String[] words = searchText.split("\\s+") ;  //split text at spaces..
        
        for( String s : words )
        {
            //System.out.println(s);
            if( !str.contains(s) )
            {
                return false ;
            }
        }
        
        return true ;
    }
    
    
    
    
    
    private static boolean isUrl(String url)
    {
        if( !url.contains("http://") && !url.contains("www.") && !url.contains("https://") )
            return false;
        
        if( url.contains(".jpeg") || url.contains(".pdf") || url.contains("@") || url.contains("adfad") || url.contains(":80") || url.contains("fdafd") || url.contains(".jpg") )
        {
            return false;
        }
        
        return true;
        
    }
    
    
    
    
    
    
    
    private static boolean isImageUrl(String url)
    {
        if( url.contains("http://") || url.contains("www.") || url.contains("https://") || ! url.contains(".pdf") )
        //if( url.endsWith(".jpg") || url.endsWith(".png") )
        {
            return true;
        }
        
        return false;
    }
    
    
    
    
    
    
    
    
    
    
    private static void crawl() throws Exception
    {
        
        while( true )
        {
            if( ! crawler() )
            {
                break;
            }
            //printUrl();
        }
        
        //printUrl();
    }
    
    
    
    
    
    
    
    
    
    
    
    private static boolean crawler() throws Exception
    {
        boolean flag = false ;
        
        for(int i=0 ; i<len ; i++ )
        {
            if( ! marked.get(i) )
            {
                marked.add(i, Boolean.TRUE);
                flag = true ;
                
                //if( choice == 1 )
                    processUrl(urlArr.get(i));
                /*if( choice == 2 )
                    downloadImages(urlArr.get(i));*/
            }
        }
        
        if( flag )
        {
            return true ;
        }
        
        return false;
    }
    
    
    
    
                                        /*
                                         * crawl url's on given page..
                                        */
    
    
    
    
    private static void processUrl(String url) throws Exception
    {
        //validate link
        if( ! isUrl(url) )
            return;
        
        if( url.endsWith("/") )
            url = url.substring(0,url.length()-1) ;
        
        
        if( searchText == null )
            System.out.println("Crawling : "+ url);
        
        //searching for text in url
        if( searchText != null )
        if( url.equals(initUrl) || matchText( url.toLowerCase() ) )
        {
            System.out.println("Crawling : "+ url);
        }
        
        /*
        if( searchText != null )
        if( ! url.equals(initUrl) && ! matchText( url.toLowerCase() ) )
        {
            return ;
        }*/
        
        if( url.startsWith("https://") )
        {
            httpsConn(url) ;         //using secure connection  
            return;
        }
        
        
        if( choice == 2 )            //download images..
        {
            if( searchText != null )
            {
                if( matchText( url.toLowerCase() ) )
                {
                    downloadImage(url) ;
                }
            }
            else
            {
                downloadImage(url) ;
            }
        }
        
        
        URLEncoder.encode(url,"UTF-8");
        
        Response response ;
        
        //System.out.println("Crawling : "+ url);
        
        Document doc ;
        
        try
        {
            //doc = Jsoup.connect(url).get();//getting java.net.SocketTimeoutException: connect timed out
            //doc = Jsoup.connect(url).timeout(0).userAgent("Mozilla").get(); //done...worked fine
            
            //using response to get status codes
            response = Jsoup.connect(url).timeout(0).userAgent("Mozilla").execute() ;
            //throw new org.jsoup.HttpStatusException("thrown", 400, url) ;
        }
        catch(org.jsoup.HttpStatusException ex)
        {
            System.out.println(url+" excep : "+ex);
            return;
        }
        catch(Exception e)
        {
            //e.printStackTrace();
            System.out.println(url+" excep : "+e);
            //throw(e);
            return;
        }
        
        if( response.statusCode() != 200 )
        {
            System.out.println("Status : "+ url+" : "+response.statusCode());
            return;
        }
        
        
        
        //parse
        doc = response.parse();
        
        
        //full web-page html code
        String text = doc.html();
        //System.out.println(text);
        
        /* // this worked fine.
        if(text.toLowerCase().contains("director"))
            System.out.println("  -->contains director");
        */
        /*  //this doesn't worked ,all n were null
        for( Node n : doc.body().childNodes() )
        {
            if( n instanceof TextNode )
            {
                System.out.println("    --> "+n.toString());
            }
        }
        */
        if( url == initUrl )
            System.out.println("....connected.... Parsing further.");
        
        Elements query = doc.select("a[href]");
        
        for( Element link : query)
        {
            String urlStr = link.attr("abs:href");
            if( urlStr.endsWith("/") )
                urlStr = urlStr.substring(0,urlStr.length()-1);
            
            if( isUrl(urlStr) )
            if( ! urlArr.contains(urlStr) && ! urlArr.contains( urlStr+"/") )
            {
                urlArr.add(urlStr);
                marked.add(Boolean.FALSE);
                len++;
                
            }
        }
        
    }
    
    
    
    
    
    
    public static void httpsConn(String url) throws Exception
    {
        //System.out.println("Crawling : "+ url);
        /*
        SSLContext ctx = SSLContext.getInstance("TLS") ;
        ctx.init(null, new TrustManager[] { new TrustManager() }, null);
        SSLContext.setDefault(ctx);
        
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection() ;
        conn.setHostnameVerifier(new HostVerifier() ) ;
        conn.setRequestMethod("GET") ;
        conn.connect() ;
        
        System.out.println("      Response: " + conn.getResponseCode());
        */
        URL link = new URL(url) ;
        URLConnection uc = link.openConnection() ;
        
        /*
        File tmp = File.createTempFile("html","");     //creating temperory file
        //FileInputStream fin = new FileInputStream(tmp) ;    //read or write bytes
        //FileOutputStream fout = new FileOutputStream(tmp) ;
        
        FileWriter fw = new FileWriter(tmp) ;
        */
        //get html page
        InputStreamReader inr = new InputStreamReader(uc.getInputStream()) ;
        BufferedReader in = new BufferedReader(inr) ;
        
        String input;
        
        while( ( input = in.readLine() ) != null )
        {
            //fw.append(input+"\n") ;
            //System.out.println(input);
            if( input.contains("<a href=\"") )
            {
                extractUrl(input , url) ;
            }
        }
        in.close();
        
        //fw.close();
        /*
        System.out.println("Page : ");
        
        BufferedReader inp = new BufferedReader(new FileReader(tmp) ) ;
        String str;
        while( ( str = inp.readLine() ) != null )
        {
            //System.out.println(str);
        }
        inp.close();
        */
    }
    
    
    
    
    
    private static void extractUrl( String str , String originalUrl ) throws Exception
    {
        if( str == null )
        {
            return ;
        }
        
        String url = str.substring(str.indexOf("<a href=\"") + 9 ) ;
        
        url = url.substring( 0, url.indexOf("\"") ) ;
        
        if( url.startsWith("/") )
        {
            url = originalUrl.concat(url) ;
        }
        
        if( url.endsWith("/") )
        {
            url = url.substring(0 , url.length()-1);
        }
        
        if( url == null )
            return;
        
        
        if( choice == 2 )               //downloading images
        {
            if( searchText == null )
            {
                downloadImage(url) ;            //download image.
            }
            
            if( searchText != null && isImageUrl(url) )
            {
                downloadImage(url) ;            //download image.
            }
            
            if( searchText != null && ! isImageUrl(url) && matchText( url.toLowerCase() ) )
            {
                downloadImage(url) ;            //download image.
            }
        }
        
        if( ! urlArr.contains(url) )
        {
            urlArr.add(url) ;
            marked.add(Boolean.FALSE) ;
            len++;
        }
        
        //System.out.println("Url : "+url);
    }
    
    
    
    
    
    
    public static void downloadImage(String url) throws Exception          //download images.
    {
        
        Response response ;
        
        Document doc ;
        
        try
        {
            //using response to get status codes
            response = Jsoup.connect(url).timeout(0).userAgent("Mozilla").execute() ;
        }
        catch(Exception e)
        {
            System.out.println(url+" excep : "+e);
            return;
        }
        
        //parse
        doc = response.parse();
        
        Elements imageLinks = doc.getElementsByTag("img");
        
        for( Element img : imageLinks )
        {
            String imageLink = img.absUrl("src");
            
            if( isImageUrl(imageLink) )
            {
                if( imageLink.endsWith("/") )
                    imageLink = imageLink.substring(0,imageLink.length()-1);
                
                //getting name of image
                int index = imageLink.lastIndexOf("/");
                String imageName = imageLink.substring(index);
                index = imageName.lastIndexOf(".");
                imageName = imageName.substring(0,index);
                
                //downloading image
                URL imgL = new URL(imageLink);
                
                //check if already downloaded then skip
                if( downloadedImages.contains(imageLink) )
                {
                    continue;
                }
                else
                {
                    downloadedImages.add(imageLink);
                }
                
                ReadableByteChannel rbc ;
                
                try
                {
                    rbc = Channels.newChannel(imgL.openStream()) ;
                    
                    
                    //create new dir
                    File dir = new File("/home/pradeep/prog/java/Swing/"+ (initUrl.replace("http://","")).replace("https://", "").replaceAll("/", "-").concat(".Images") ) ;
                    //if dir DNE , create
                    if( ! dir.exists() )
                    {
                        try
                        {
                            dir.mkdir();
                        }
                        catch(Exception e)
                        {
                            System.out.println("Directory Cannot be created");
                            throw e;
                        }
                    }
                
                    File f = new File(dir+"/"+imageName.replaceAll("/", ""));
                    
                    storageFile = dir;                  
                    
                    FileOutputStream fos = new FileOutputStream(f);
                    
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    
                    System.out.println("Downloaded "+imageName);
                }
                catch( FileNotFoundException fe)
                {
                    System.out.println("File Not Found : "+ imgL);
                }
                
            }
        }
    }
    
    
    
                                        /*
                                         * crawl on all url's and download images ...
                                        */
    
    
    
    
    
    
    private static void downloadImages(String url) throws Exception
    {
        //validate link
        if( ! isUrl(url) )
            return;
        
        if( url.endsWith("/") )
            url = url.substring(0,url.length()-1) ;
        
        if( url.contains("https://"))
        {
            httpsConn(url) ;
            return;
        }
        
        
        URLEncoder.encode(url,"UTF-8");
        
        Response response = null;
        
        Document doc = null;
        
        try
        {
            //using response to get status codes
            response = Jsoup.connect(url).timeout(0).userAgent("Mozilla").execute() ;
        }
        catch(org.jsoup.HttpStatusException ex)
        {
            System.out.println(url+" excep : "+ex);
            return;
        }
        catch(Exception e)
        {
            System.out.println(url+" excep : "+e);
            return;
        }
        
        if( response.statusCode() != 200 )
        {
            System.out.println("Status : "+ url+" : "+response.statusCode());
            return;
        }
        
        System.out.println("Crawling : "+ url);
        
        //parse
        doc = response.parse();
        
        Elements imageLinks = doc.getElementsByTag("img");
        
        for( Element img : imageLinks )
        {
            String imageLink = img.absUrl("src");
            
            if( isImageUrl(imageLink) )
            {
                if( imageLink.endsWith("/") )
                    imageLink = imageLink.substring(0,imageLink.length()-1);
                
                //getting name of image
                int index = imageLink.lastIndexOf("/");
                String imageName = imageLink.substring(index);
                index = imageName.lastIndexOf(".");
                imageName = imageName.substring(0,index);
                
                //downloading image
                URL imgL = new URL(imageLink);
                
                //check if already downloaded then skip
                if( downloadedImages.contains(imageLink) )
                {
                    continue;
                }
                else
                {
                    downloadedImages.add(imageLink);
                }
                
                ReadableByteChannel rbc = Channels.newChannel(imgL.openStream()) ;
                
                //create new dir
                File dir = new File("/home/pradeep/prog/java/Swing/"+ (initUrl.replace("http://","")).replace("https://", "").replaceAll("/", "-").concat(".Images") ) ;
                //if dir DNE , create
                if( ! dir.exists() )
                {
                    try
                    {
                        dir.mkdir();
                    }
                    catch(Exception e)
                    {
                        System.out.println("Directory Cannot be created");
                        throw e;
                    }
                }
                
                File f = new File(dir+"/"+imageName.replaceAll("/", ""));
                
                storageFile = dir;
                
                FileOutputStream fos = new FileOutputStream(f);
                
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                
                System.out.println("Downloaded "+imageName);
            }
        }
        
        
              // check for all urls in page
        Elements query = doc.select("a[href]");
        
        for( Element link : query)
        {
            String urlStr = link.attr("abs:href");
            if( urlStr.endsWith("/") )
                urlStr = urlStr.substring(0,urlStr.length()-1);
            
            if( isUrl(urlStr) )
            if( ! urlArr.contains(urlStr) && ! urlArr.contains( urlStr+"/") )
            {
                urlArr.add(urlStr);
                marked.add(Boolean.FALSE);
                len++;
                
            }
        }
        
    }
    
    
    
    
    
            //open webpage in default browser..
    
    public static void openInBrowser(String url) throws Exception         
    {
        URI uri = new URI(url) ;
        
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null ;
        
        if( desktop != null && desktop.isSupported(Desktop.Action.BROWSE) )
        {
            try
            {
                desktop.browse(uri);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    
    
    
    
    public static void openFolder(File file) throws Exception
    {
        Desktop desk = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null ;
        
        if( desk != null && desk.isSupported(Desktop.Action.OPEN) )
        {
            try
            {
                desk.open(file);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    
    
    
    private static void printUrl()
    {
        System.out.println("Urls found are :\n");
        
        for(int i=0;i<len;i++)
        {
            System.out.println(urlArr.get(i));
        }
    }
    
}