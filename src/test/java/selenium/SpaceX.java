package selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.opencsv.CSVReader;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class SpaceX {
    WebDriver driver;
    List<String[]> data ;
    int count,productCount;

    @BeforeClass
    public void before() {
        //point to the datasheet
        data = readFromCSVFile("./src/test/resources/data.csv");
    }

    @Test
    public void spaceX() throws IOException, InterruptedException {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        count=0;
        for (String[] row : data) {
            //skip first row
            // the datasheet hearders
            if(count==0){
                count++;
                continue;
            }
            flow(row);
            count++;
        }

    }
    public void flow(String [] row) throws InterruptedException {
        selectBrowser(row[0]);
        lauchSpaceXHomePage();
        searchForProduct("Space");
        getProduct(row[1],row[2],row[3]);
    }

    public void lauchSpaceXHomePage(){
        driver.get("https://shop.spacex.com/");
        driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
        String actualTitle = driver.getTitle();

        String expectedTitle = "Official SpaceX Store";

        assertEquals(expectedTitle, actualTitle);
    }
    public void selectBrowser(String browser){
        switch(browser.toUpperCase()){
            case "CHROME":
                driver= new ChromeDriver();
                break;
            case "FIREFOX":
                driver= new FirefoxDriver();
                break;
            case "EDGE":
                driver= new EdgeDriver();
                break;
            default:
                fail(browser +" is none of the defined browsers");
                break;
        }
    }
    public void searchForProduct(String product) throws InterruptedException {//*[@id="section-header"]/div/div[4]/button
        driver.findElement(By.xpath("//*[@id='section-header']/div/div[4]/button")).click();
        driver.findElement(By.xpath("//*[@id='search-input']")).sendKeys(product);
        productCount=1;
        Thread.sleep(3000);
        while(driver.findElements(By.xpath("//*[@id='Search']/div/div[2]/div/div[2]/div/div["+productCount+"]/div/div/div/span/a")).size()>0){
            System.out.print(driver.findElement(By.xpath("//*[@id='Search']/div/div[2]/div/div[2]/div/div["+productCount+"]/div/div/div/span/a")).getText()+" ");
            System.out.println(driver.findElement(By.xpath("//*[@id='Search']/div/div[2]/div/div[2]/div/div["+productCount+"]/div/div/div/div/span/span")).getText()+ " ");
            productCount++;
        }


    }
    public void getProduct(String product,String price, String quantity) throws InterruptedException {
        driver.findElement(By.xpath("//*[@id='section-header']/div/div[4]/button")).click();
        searchForProduct(product);
        for(int i=1;i<=productCount;i++){
            if(driver.findElement(By.xpath("//*[@id='Search']/div/div[2]/div/div[2]/div/div["+i+"]/div/div/div/span/a")).getText().equalsIgnoreCase(product)){
                driver.findElement(By.xpath("//*[@id='Search']/div/div[2]/div/div[2]/div/div["+i+"]/div/div/div/span/a")).click();
                break;
            }
        }
        String expectedProduct=driver.findElement(By.xpath("//*[@id='shopify-section-product-template']/section/div[1]/div[2]/div[1]/div/div[1]/h1")).getText();
        String expectedPrice=driver.findElement(By.xpath("//*[@id='shopify-section-product-template']/section/div[1]/div[2]/div[1]/div/div[1]/div[1]/span/span")).getText();
        assertEquals(expectedProduct, product.toUpperCase());
        assertEquals(expectedPrice, price);
        Thread.sleep(5000);
        for(int i=1;i<=Integer.parseInt(quantity);i++){
            if(i<Integer.parseInt(quantity)){             //*[@id="product_form_6537598271567"]/div[1]/div[4]/div/span[2]
                driver.findElement(By.xpath("//*[@id='product_form_6537598271567']/div[1]/div[4]/div/span[2]")).click();
            }
        }
        Thread.sleep(5000);
        driver.findElement(By.xpath("//*[@id='product_form_6537598271567']/button/span")).click();
        double total = Double.parseDouble(price.substring(1))* Integer.parseInt(quantity);
        double expTotal =Double.parseDouble(driver.findElement(By.xpath("//*[@id='shopify-section-cart-template']/section/div/div/form/footer/div/p[1]/span")).getText().substring(1));
        assertEquals(expTotal,total);

    }
    @AfterClass
    public void after() {
        System.out.println(count -1+" TEST(S) EXECUTED!!!!!");
        driver.quit();
    }
    private List<String[]> readFromCSVFile(String csvFilePath) {
        try {
            CSVReader reader = new CSVReader(new FileReader(csvFilePath));
            List<String[]> data = reader.readAll();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
