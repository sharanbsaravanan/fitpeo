package com.fitpeo;

import dev.failsafe.internal.util.Assert;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Fitpeo {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().browserVersion("127.0.6533.89").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--allow-insecure-localhost");
        options.addArguments("--remote-allow-origins=*");
        //Initializing chrome driver
        WebDriver driver = new ChromeDriver(options);
        //opening fitpeo webpage
        driver.get("https://fitpeo.com/");
        //maximizing the webpage to get the full view
        driver.manage().window().maximize();
        //defaulting the wait to 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        //Click on Revenue calculator tab
        WebElement revenueCalculatorLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/revenue-calculator']")));
        revenueCalculatorLink.click();
        //Get the webElement of slider Section to scroll till that slider div element
        WebElement sliderSection = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.MuiGrid-root.MuiGrid-container.MuiGrid-spacing-xs-6.css-l0ykmo")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sliderSection);
        //Get the webElement of slider to slide
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".MuiSlider-thumb")));
        System.out.println("Position of X before sliding"+slider.getLocation().getX());
        Actions actions = new Actions(driver);
        actions.dragAndDropBy(slider, 93,0).perform(); // this sets the value to 816 and offset 94 sets value to 823
        //Get the value from input text box and perform right arrow key till 820 value is reached
        WebElement sliderInputTextBox = driver.findElement(By.cssSelector("input[type='number'].MuiInputBase-input.MuiOutlinedInput-input.MuiInputBase-inputSizeSmall.css-1o6z5ng"));
        actions.click(slider).perform(); // Click on the slider thumb
        // Perform multiple right arrow key presses to move the slider to the desired value
        while(!sliderInputTextBox.getAttribute("value").equals("820")){
            actions.sendKeys(Keys.ARROW_RIGHT).perform();
        }
        //Get the value of slider position after updating to 820 to compare once changed to 560
        int sliderPostion= slider.getLocation().getX();
        System.out.println("Position of X after sliding"+slider.getLocation().getX());
        //Set the value of 560 to the input element
        sliderInputTextBox.click();
        String value = sliderInputTextBox.getAttribute("value");
        for (int i = value.length(); i > 0; i--) {
            actions.sendKeys(Keys.BACK_SPACE).perform();
        }
        sliderInputTextBox.sendKeys("560");
        //Get the value of slider Position to compare after changing to 560
        int currentSliderPosition = slider.getLocation().getX();
        System.out.println("Positon of X after setting value to 560 "+ currentSliderPosition);
        //Assertion to compare the postions before and after change
        Assert.isTrue(currentSliderPosition<sliderPostion,"Slider position didnot change");
        //since step 9 shows the screenshot of 820 patients. resetting the value back to 820
        sliderInputTextBox.click();
        value = sliderInputTextBox.getAttribute("value");
        for (int i = value.length(); i > 0; i--) {
            actions.sendKeys(Keys.BACK_SPACE).perform();
        }
        sliderInputTextBox.sendKeys("820");
        //Get the top Div of all the boxes
        WebElement topDiv = driver.findElement(By.cssSelector("div.MuiBox-root.css-1p19z09"));
        //Get all the div from the parent div
        List<WebElement> allDivs = topDiv.findElements(By.cssSelector("div.MuiBox-root.css-4o8pys"));
        //cpt values to be checked in list
        List<String> cptValues = Arrays.asList("CPT-99091", "CPT-99453","CPT-99454", "CPT-99474");
        for(WebElement element : allDivs){
            WebElement paragraph = element.findElement(By.cssSelector("p.MuiTypography-root.MuiTypography-body1.inter.css-1s3unkt"));
            //if the current cpt value present in the list, click the check box
            if(cptValues.contains(paragraph.getText())){
                WebElement checkBoxElement = element.findElement(By.cssSelector("input[type='checkbox'].PrivateSwitchBase-input.css-1m9pwf3"));
                checkBoxElement.click();
            }
        }
        //Get the element of Header once it is visible
        WebElement headerElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("header.MuiPaper-root.MuiPaper-elevation.MuiPaper-elevation4.MuiAppBar-root.MuiAppBar-colorDefault.MuiAppBar-positionFixed.mui-fixed.css-nq2yav")));
        List<WebElement> paraElements = headerElement.findElements(By.cssSelector("p.MuiTypography-root.MuiTypography-body2.inter.css-1xroguk"));
        for(WebElement para : paraElements){
            System.out.println(para.getText());
            //Get the paragraph element which contains the below text
            if(para.getText().contains("Total Recurring Reimbursement for all Patients Per Month:")){
                WebElement reimbursementElement = para.findElement(By.tagName("p"));
                System.out.println(reimbursementElement.getText());
                Assert.isTrue(reimbursementElement.getText().equals("$110700"),"Reimbursement value is not equal to $110700");
            }
        }
        driver.quit();
    }
}